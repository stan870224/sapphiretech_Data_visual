from flask import Flask, jsonify, request
from flask_cors import CORS
import pyodbc
import pandas as pd
import os
from datetime import datetime

app = Flask(__name__)
CORS(app, origins=["*"])

# 資料庫設定
DB_CONFIG = {
    'server': '吳瑋倫',
    'database': 'SapphireRMA',
    'username': 'sa2',
    'password': '1qaz2wsx'
}

def get_db_connection():
    try:
        connection_string = f"DRIVER={{SQL Server}};SERVER={DB_CONFIG['server']};DATABASE={DB_CONFIG['database']};UID={DB_CONFIG['username']};PWD={DB_CONFIG['password']}"
        return pyodbc.connect(connection_string)
    except Exception as e:
        print(f"資料庫連線失敗: {e}")
        return None

def clean_data(df):
    """清理資料"""
    df = df.astype(str)
    df = df.replace('nan', '')
    df = df.fillna('')
    return df


def process_rma_data(product_type):
    """處理 RMA 資料"""
    rma_file = f'../data/{product_type}_RMA_record.xlsx'
    
    if not os.path.exists(rma_file):
        return None, f"找不到檔案: {product_type}_RMA_record.xlsx"
    
    try:
        # 先讀取 Excel 檔案看工作表
        excel_file = pd.ExcelFile(rma_file)
        print(f"📋 {product_type} 工作表名稱: {excel_file.sheet_names}")
        
        # 統一讀取 'TW RMA history' 頁籤
        if 'TW RMA history' in excel_file.sheet_names:
            df = pd.read_excel(rma_file, sheet_name='TW RMA history')
        else:
            df = pd.read_excel(rma_file, sheet_name=0)
            
        df = clean_data(df)
        
        column_mapping = {
            'Rma No': 'Rma_No',
            'Customer Name': 'Customer_Name',
            'Serial No (可用掃碼)': 'Serial_No',
            'Part No(可用掃碼)': 'PN',
            'SKU#(可用掃碼)': 'SKU',
            'Product Name': 'Product_Name',
            'Sell/Ship Date': 'Sell_Ship_Date',
            'Create Date ': 'Create_Date',
            'Return Date': 'Return_Date',
            'Failure desc': 'Failure_desc',
            'VI Damage Status': 'VI_Damage_Status',
            'Test Result Desc': 'Test_Result_Desc',
            'Replacement SN in TW(可用掃碼)': 'Replacement_SN_in_TW',
            'Replacement PN in TW(可用掃碼)': 'Replacement_PN_in_TW',
            'Replacement SKU# in TW(可用掃碼)': 'Replacement_SKU_in_TW',
            'Replacement SN from HK': 'Replacement_SN_from_HK',
            'Replacement PN from HK': 'Replacement_PN_from_HK',
            'Replacement SKU# from HK': 'Replacement_SKU_from_HK',
            'RMA board Test Result': 'RMA_board_Test_Result',
            'End user invoice date ': 'End_user_invoice_date',
            'Warranty Until ': 'Warranty_Until',
            ' Remark': 'Remark'
        }
        
        df = df.rename(columns=column_mapping)
        
        # 檢查 Serial_No 是否存在
        if 'Serial_No' not in df.columns:
            return None, f"欄位 'Serial_No' 不存在，可用欄位: {list(df.columns)}"
        
        df = df[df['Serial_No'].notna() & (df['Serial_No'] != '')]
        
        return df, None
        
    except Exception as e:
        return None, f"RMA 檔案處理失敗: {str(e)}"

def process_stock_data(product_type):
    """處理庫存資料"""
    stock_file = f'../data/{product_type}_buffer_stock.xlsx'
    
    if not os.path.exists(stock_file):
        return None, f"找不到檔案: {product_type}_buffer_stock.xlsx"
    
    try:
        df = pd.read_excel(stock_file, sheet_name=0)
        df = clean_data(df)
        
        column_mapping = {
            'Prodcut name': 'Prodcut_name',
            'PN#': 'PN',
            'SKU#': 'SKU',
            'S/N': 'Serial_No'
        }
        
        df = df.rename(columns=column_mapping)
        df = df[df['Serial_No'].notna() & (df['Serial_No'] != '')]
        
        # 不包含 Quantity
        if 'Quantity' in df.columns:
            df = df.drop('Quantity', axis=1)
        
        return df, None
        
    except Exception as e:
        return None, f"庫存檔案處理失敗: {str(e)}"

def insert_to_db(conn, df, table_name):
    """插入或更新資料到資料庫"""
    if df.empty:
        return {'inserted': 0, 'updated': 0}
    
    cursor = conn.cursor()
    inserted_count = 0
    updated_count = 0
    
    try:
        for _, row in df.iterrows():
            check_sql = f"SELECT COUNT(*) FROM {table_name} WHERE Serial_No = ?"
            cursor.execute(check_sql, row['Serial_No'])
            exists = cursor.fetchone()[0] > 0
            
            if exists:
                columns = [col for col in df.columns if col != 'Serial_No']
                set_clause = ', '.join([f"{col} = ?" for col in columns])
                update_sql = f"UPDATE {table_name} SET {set_clause} WHERE Serial_No = ?"
                
                values = [row[col] for col in columns] + [row['Serial_No']]
                cursor.execute(update_sql, values)
                updated_count += 1
            else:
                columns = ', '.join(df.columns)
                placeholders = ', '.join(['?' for _ in df.columns])
                insert_sql = f"INSERT INTO {table_name} ({columns}) VALUES ({placeholders})"
                
                values = [row[col] for col in df.columns]
                cursor.execute(insert_sql, values)
                inserted_count += 1
        
        conn.commit()
        return {'inserted': inserted_count, 'updated': updated_count}
        
    except Exception as e:
        conn.rollback()
        raise e
    finally:
        cursor.close()

@app.route('/')
def hello():
    return "RMA 批次執行 API"

@app.route('/batch-execute', methods=['POST'])
def batch_execute():
    """批次執行指定產品線"""
    try:
        data = request.get_json()
        product_type = data.get('product_type')
        
        if not product_type:
            return jsonify({'success': False, 'message': '請選擇產品線'})
        
        if product_type not in ['VGA', 'MB', 'MiniPC']:
            return jsonify({'success': False, 'message': '無效的產品線'})
        
        conn = get_db_connection()
        if not conn:
            return jsonify({'success': False, 'message': '資料庫連線失敗'})
        
        print(f"\n🔄 開始處理 {product_type}...")
        
        # 檢查檔案並處理
        rma_df, rma_error = process_rma_data(product_type)
        stock_df, stock_error = process_stock_data(product_type)
        
        # 如果兩個檔案都沒有
        if rma_df is None and stock_df is None:
            conn.close()
            return jsonify({
                'success': False, 
                'message': f'{product_type} 產品線沒有找到任何檔案',
                'details': {
                    'rma_error': rma_error,
                    'stock_error': stock_error
                }
            })
        
        # 處理 RMA 資料
        if rma_df is not None:
            rma_result = insert_to_db(conn, rma_df, f'{product_type}_RMA_record')
            rma_message = f"RMA: 新增 {rma_result['inserted']} 筆，更新 {rma_result['updated']} 筆"
        else:
            rma_result = {'inserted': 0, 'updated': 0}
            rma_message = f"RMA: {rma_error}"
        
        # 處理庫存資料
        if stock_df is not None:
            stock_result = insert_to_db(conn, stock_df, f'{product_type}_buffer_stock')
            stock_message = f"庫存: 新增 {stock_result['inserted']} 筆，更新 {stock_result['updated']} 筆"
        else:
            stock_result = {'inserted': 0, 'updated': 0}
            stock_message = f"庫存: {stock_error}"
        
        conn.close()
        
        return jsonify({
            'success': True,
            'message': f'{product_type} 產品線處理完成',
            'product_type': product_type,
            'rma_stats': {
                'inserted': rma_result['inserted'],
                'updated': rma_result['updated'],
                'total': rma_result['inserted'] + rma_result['updated'],
                'message': rma_message
            },
            'stock_stats': {
                'inserted': stock_result['inserted'],
                'updated': stock_result['updated'],
                'total': stock_result['inserted'] + stock_result['updated'],
                'message': stock_message
            }
        })
        
    except Exception as e:
        return jsonify({'success': False, 'message': f'處理失敗: {str(e)}'})

if __name__ == '__main__':
    app.run(debug=True, port=5000)