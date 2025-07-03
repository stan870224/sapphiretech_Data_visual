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

  // RMA 資料相關 API (預留)
  rma = {
    // 查詢 RMA 資料
    search: (params) => this.post('/rma/search', params),
    
    // 建立 RMA 記錄
    create: (data) => this.post('/rma/create', data),
    
    // 更新 RMA 記錄
    update: (id, data) => this.post(`/rma/update/${id}`, data)
  };

  // 庫存資料相關 API (預留)
  stock = {
    // 查詢庫存
    search: (params) => this.post('/stock/search', params),
    
    // 更新庫存
    update: (data) => this.post('/stock/update', data)
  };
}

// 建立全域 API 實例
const api = new ApiService();

// 如果在 Node.js 環境中使用
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { ApiService, api };
}