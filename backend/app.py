import argparse
import pyodbc
import pandas as pd
import os
import json
import sys
from datetime import datetime

class BatchProcessor:
    def __init__(self, databaseUrl, databaseUser, databasePassword):
        self.databaseConfig = self.parseDatabaseUrl(databaseUrl, databaseUser, databasePassword)
    
    def parseDatabaseUrl(self, jdbcUrl, username, password):
        """解析 JDBC URL 並建立資料庫配置"""
        try:
            urlParts = jdbcUrl.replace('jdbc:sqlserver://', '').split(';')
            serverInfo = urlParts[0]
            
            databaseName = 'SapphireRMA'  
            for part in urlParts[1:]:
                if part.startswith('databaseName='):
                    databaseName = part.split('=')[1]
                    break
            
            return {
                'server': serverInfo,  # 保留完整的 server:port
                'database': databaseName,
                'username': username,
                'password': password
            }
        except Exception as e:
            print(f"解析資料庫 URL 失敗: {e}")
            # 使用原始配置作為備用
            return {
                'server': 'localhost:1434',
                'database': 'SapphireRMA',
                'username': 'sa2',
                'password': '1qaz2wsx'
            }
    
    def getDatabaseConnection(self):
        """建立資料庫連線"""
        try:
            server = self.databaseConfig['server'].replace(':1433', '')
            
            connectionString = f"""
            DRIVER={{SQL Server}};
            SERVER={server},1433;
            DATABASE={self.databaseConfig['database']};
            UID={self.databaseConfig['username']};
            PWD={self.databaseConfig['password']};
            TrustServerCertificate=yes;
            """
            connection = pyodbc.connect(connectionString)
            connection.autocommit = False  # 確保交易控制
            return connection
        except Exception as e:
            print(f"資料庫連線失敗: {e}")
            return None
        
    def getAvailableProductLines(self):
        """從資料庫取得可用的產品線"""
        connection = self.getDatabaseConnection()
        if not connection:
            return []
        
        try:
            cursor = connection.cursor()
            cursor.execute("SELECT product_line FROM product_lines ORDER BY id")
            productLines = [row[0] for row in cursor.fetchall()]
            return productLines
        except Exception as e:
            print(f"取得產品線失敗: {e}")
            return []
        finally:
            connection.close()
    
    def isValidProductLine(self, productType):
        """驗證產品線是否存在"""
        availableProductLines = self.getAvailableProductLines()
        return productType in availableProductLines
    
    def cleanData(self, dataFrame):
        """清理資料"""
        dataFrame = dataFrame.astype(str)
        dataFrame = dataFrame.replace('nan', '')
        dataFrame = dataFrame.fillna('')
        return dataFrame
    
    def processRmaData(self, productType):
        """處理 RMA 資料"""
        rmaFilePath = f'../data/{productType}_RMA_record.xlsx'
        
        if not os.path.exists(rmaFilePath):
            return None, f"找不到檔案: {productType}_RMA_record.xlsx"
        
        try:
            # 先讀取 Excel 檔案看工作表
            excelFile = pd.ExcelFile(rmaFilePath)
            print(f"📋 {productType} 工作表名稱: {excelFile.sheet_names}")
            
            # 統一讀取 'TW RMA history' 頁籤
            if 'TW RMA history' in excelFile.sheet_names:
                dataFrame = pd.read_excel(rmaFilePath, sheet_name='TW RMA history')
            else:
                dataFrame = pd.read_excel(rmaFilePath, sheet_name=0)
                
            dataFrame = self.cleanData(dataFrame)
            
            columnMapping = {
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
            
            dataFrame = dataFrame.rename(columns=columnMapping)
            
            # 檢查 Serial_No 是否存在
            if 'Serial_No' not in dataFrame.columns:
                return None, f"欄位 'Serial_No' 不存在，可用欄位: {list(dataFrame.columns)}"
            
            dataFrame = dataFrame[dataFrame['Serial_No'].notna() & (dataFrame['Serial_No'] != '')]
            
            return dataFrame, None
            
        except Exception as e:
            return None, f"RMA 檔案處理失敗: {str(e)}"
    
    def processStockData(self, productType):
        """處理庫存資料"""
        stockFilePath = f'../data/{productType}_buffer_stock.xlsx'
        
        if not os.path.exists(stockFilePath):
            return None, f"找不到檔案: {productType}_buffer_stock.xlsx"
        
        try:
            dataFrame = pd.read_excel(stockFilePath, sheet_name=0)
            dataFrame = self.cleanData(dataFrame)
            
            columnMapping = {
                'Prodcut name': 'Prodcut_name',
                'PN#': 'PN',
                'SKU#': 'SKU',
                'S/N': 'Serial_No'
            }
            
            dataFrame = dataFrame.rename(columns=columnMapping)
            dataFrame = dataFrame[dataFrame['Serial_No'].notna() & (dataFrame['Serial_No'] != '')]
            
            # 不包含 Quantity
            if 'Quantity' in dataFrame.columns:
                dataFrame = dataFrame.drop('Quantity', axis=1)
            
            return dataFrame, None
            
        except Exception as e:
            return None, f"庫存檔案處理失敗: {str(e)}"
    
    def insertToDatabase(self, connection, dataFrame, tableName):
        """插入或更新資料到資料庫"""
        if dataFrame.empty:
            return {'inserted': 0, 'updated': 0}
        
        cursor = connection.cursor()
        insertedCount = 0
        updatedCount = 0
        
        try:
            for _, row in dataFrame.iterrows():
                checkSql = f"SELECT COUNT(*) FROM {tableName} WHERE Serial_No = ?"
                cursor.execute(checkSql, row['Serial_No'])
                recordExists = cursor.fetchone()[0] > 0
                
                if recordExists:
                    columns = [col for col in dataFrame.columns if col != 'Serial_No']
                    setClause = ', '.join([f"{col} = ?" for col in columns])
                    updateSql = f"UPDATE {tableName} SET {setClause} WHERE Serial_No = ?"
                    
                    values = [row[col] for col in columns] + [row['Serial_No']]
                    cursor.execute(updateSql, values)
                    updatedCount += 1
                else:
                    columns = ', '.join(dataFrame.columns)
                    placeholders = ', '.join(['?' for _ in dataFrame.columns])
                    insertSql = f"INSERT INTO {tableName} ({columns}) VALUES ({placeholders})"
                    
                    values = [row[col] for col in dataFrame.columns]
                    cursor.execute(insertSql, values)
                    insertedCount += 1
            
            connection.commit()
            return {'inserted': insertedCount, 'updated': updatedCount}
            
        except Exception as e:
            connection.rollback()
            raise e
        finally:
            cursor.close()
    
    def executeBatchProcess(self, productType):
        """執行批次處理"""
        # 先驗證產品線是否存在於資料庫中
        if not self.isValidProductLine(productType):
            availableProductLines = self.getAvailableProductLines()
            return {
                'success': False,
                'message': f'無效的產品線: {productType}，可用的產品線: {availableProductLines}',
                'productType': productType
            }

        print(f"\n 開始處理 {productType}...")

        # 檢查檔案並處理
        rmaDataFrame, rmaError = self.processRmaData(productType)
        stockDataFrame, stockError = self.processStockData(productType)

        # 如果兩個檔案都沒有
        if rmaDataFrame is None and stockDataFrame is None:
            return {
                'success': False,
                'message': f'{productType} 產品線沒有找到任何檔案',
                'productType': productType,
                'details': {
                    'rmaError': rmaError,
                    'stockError': stockError
                }
            }

        # 為每個操作建立獨立的連線
        try:
            # 處理 RMA 資料
            if rmaDataFrame is not None:
                connection = self.getDatabaseConnection()
                if not connection:
                    return {'success': False, 'message': '資料庫連線失敗', 'productType': productType}

                try:
                    rmaResult = self.insertToDatabase(connection, rmaDataFrame, f'{productType}_RMA_record')
                    rmaMessage = f"RMA: 新增 {rmaResult['inserted']} 筆，更新 {rmaResult['updated']} 筆"
                finally:
                    connection.close()
            else:
                rmaResult = {'inserted': 0, 'updated': 0}
                rmaMessage = f"RMA: {rmaError}"

            # 處理庫存資料
            if stockDataFrame is not None:
                connection = self.getDatabaseConnection()
                if not connection:
                    return {'success': False, 'message': '資料庫連線失敗', 'productType': productType}

                try:
                    stockResult = self.insertToDatabase(connection, stockDataFrame, f'{productType}_buffer_stock')
                    stockMessage = f"庫存: 新增 {stockResult['inserted']} 筆，更新 {stockResult['updated']} 筆"
                finally:
                    connection.close()
            else:
                stockResult = {'inserted': 0, 'updated': 0}
                stockMessage = f"庫存: {stockError}"

            return {
                'success': True,
                'message': f'{productType} 產品線處理完成',
                'productType': productType,
                'rmaStats': {
                    'inserted': rmaResult['inserted'],
                    'updated': rmaResult['updated'],
                    'total': rmaResult['inserted'] + rmaResult['updated'],
                    'message': rmaMessage
                },
                'stockStats': {
                    'inserted': stockResult['inserted'],
                    'updated': stockResult['updated'],
                    'total': stockResult['inserted'] + stockResult['updated'],
                    'message': stockMessage
                }
            }

        except Exception as e:
            return {
                'success': False,
                'message': f'處理失敗: {str(e)}',
                'productType': productType
            }

def main():
    """主函數"""
    try:
        parser = argparse.ArgumentParser(description='RMA 批次處理腳本')
        parser.add_argument('--db-url', required=True, help='資料庫 JDBC URL')
        parser.add_argument('--db-user', required=True, help='資料庫使用者名稱')
        parser.add_argument('--db-password', required=True, help='資料庫密碼')
        parser.add_argument('--product-type', required=True, help='產品類型')
        args = parser.parse_args()
        
        # 建立批次處理器
        batchProcessor = BatchProcessor(args.db_url, args.db_user, args.db_password)
        
        # 執行批次處理
        result = batchProcessor.executeBatchProcess(args.product_type)
        
        # 輸出結果 (Java 會讀取這個 JSON)
        print(json.dumps(result, ensure_ascii=False, indent=2))
        
        # 根據處理結果設定退出碼
        if result['success']:
            sys.exit(0)
        else:
            sys.exit(1)
            
    except Exception as e:
        errorResult = {
            'success': False,
            'message': f'腳本執行失敗: {str(e)}',
            'productType': None
        }
        print(json.dumps(errorResult, ensure_ascii=False, indent=2))
        sys.exit(1)

if __name__ == "__main__":
    main()