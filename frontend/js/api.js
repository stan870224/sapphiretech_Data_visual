// API 呼叫封裝
const API_BASE_URL = 'http://localhost:8080/api';

class ApiService {
  
  // 通用 API 呼叫方法
  async request(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      },
      ...options
    };

    try {
      const response = await fetch(url, config);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      return data;
    } catch (error) {
      console.error('API 呼叫錯誤:', error);
      throw error;
    }
  }

  // GET 請求
  async get(endpoint) {
    return this.request(endpoint, { method: 'GET' });
  }

  // POST 請求
  async post(endpoint, data) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(data)
    });
  }

  // PUT 請求
  async put(endpoint, data) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(data)
    });
  }

  // DELETE 請求
  async delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  }

  // 批次處理相關 API
  batch = {
    // 取得產品線列表
    getProductLines: () => this.get('/batch/product-lines'),
    
    // 執行批次處理
    execute: (productType) => this.post('/batch/execute', { productType }),
    
    // 初始化產品線
    initProductLines: () => this.post('/batch/init-product-lines'),
    
    // 健康檢查
    health: () => this.get('/batch/health'),
    
    // 驗證產品線
    validateProductLine: (productType) => this.get(`/batch/validate-product-line/${productType}`)
  };

  // RMA 資料相關 API
  rma = {
    // 查詢 RMA 資料
    search: (params) => this.post('/rma/search', params),
    
    // 搜尋用於更新的資料
    searchForUpdate: (params) => {
      const queryString = new URLSearchParams(params).toString();
      return this.get(`/rma/search-for-update?${queryString}`);
    },
    
    // 建立 RMA 記錄
    create: (data) => this.post('/rma/create', data),
    
    // 更新 RMA 記錄
    update: (data) => this.put('/rma/update', data),
    
    // 刪除 RMA 記錄
    delete: (productType, serialNo) => this.delete(`/rma/${productType}/${serialNo}`),
    
    // 檢查記錄是否存在
    exists: (productType, serialNo) => this.get(`/rma/exists/${productType}/${serialNo}`),
    
    // 取得產品線列表
    getProductLines: () => this.get('/rma/product-lines'),
    
    // 驗證產品線
    validateProductLine: (productType) => this.get(`/rma/validate-product-line/${productType}`),
    
    // 健康檢查
    health: () => this.get('/rma/health')
  };

  // 庫存資料相關 API
  stock = {
    // 取得所有庫存
    getAll: (productType) => this.get(`/stock/${productType}`),
    
    // 查詢庫存
    search: (params) => this.post('/stock/search', params),
    
    // 取得庫存詳細資料
    getDetail: (productType, serialNo) => this.get(`/stock/${productType}/detail/${serialNo}`),
    
    // 建立庫存記錄
    create: (data) => this.post('/stock/create', data),
    
    // 更新庫存記錄
    update: (data) => this.put('/stock/update', data),
    
    // 刪除庫存記錄
    delete: (productType, serialNo) => this.delete(`/stock/${productType}/${serialNo}`),
    
    // 關鍵字搜尋
    searchByKeyword: (productType, keyword) => this.get(`/stock/${productType}/search-keyword?keyword=${encodeURIComponent(keyword)}`),
    
    // 根據 P/N 搜尋
    searchByPN: (productType, pn) => this.get(`/stock/${productType}/search-pn?pn=${encodeURIComponent(pn)}`),
    
    // 根據 SKU 搜尋
    searchBySKU: (productType, sku) => this.get(`/stock/${productType}/search-sku?sku=${encodeURIComponent(sku)}`),
    
    // 取得統計資料
    getStats: (productType) => this.get(`/stock/${productType}/stats`),
    
    // 批次刪除
    batchDelete: (productType, serialNos) => this.post(`/stock/${productType}/batch-delete`, serialNos),
    
    // 檢查記錄是否存在
    exists: (productType, serialNo) => this.get(`/stock/exists/${productType}/${serialNo}`),
    
    // 健康檢查
    health: () => this.get('/stock/health')
  };

  // 檔案上傳相關 API
  upload = {
    // 上傳檔案
    uploadFile: async (file) => {
      const formData = new FormData();
      formData.append('file', file);
      
      try {
        const response = await fetch(`${API_BASE_URL}/upload/file`, {
          method: 'POST',
          body: formData
        });
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
      } catch (error) {
        console.error('檔案上傳錯誤:', error);
        throw error;
      }
    },
    
    // 取得檔案列表
    getFiles: () => this.get('/upload/files'),
    
    // 刪除檔案
    deleteFile: (filename) => this.delete(`/upload/files/${encodeURIComponent(filename)}`),
    
    // 健康檢查
    health: () => this.get('/upload/health')
  };
}

// 建立全域 API 實例
const api = new ApiService();

// 如果在 Node.js 環境中使用
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { ApiService, api };
}