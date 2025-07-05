// BatchExecution 頁面組件
const BatchExecution = {
  template: `
    <div class="batch-execution-page">
      <h2>批次執行</h2>
      
      <!-- 操作說明 -->
      <div class="operation-guide">
        <div class="guide-icon">📋</div>
        <div class="guide-content">
          <h3>操作說明</h3>
          <p>1. 上傳 Excel 檔案到系統，或將檔案放置到 <code>data/</code> 資料夾中</p>
          <p>2. 檔案命名格式：<code>{產品線}_RMA_record.xlsx</code> 和 <code>{產品線}_buffer_stock.xlsx</code></p>
          <p>3. 選擇要處理的產品線</p>
          <p>4. 點擊「批次執行」開始處理</p>
        </div>
      </div>
      
      <!-- 檔案上傳區域 -->
      <div class="file-upload-section">
        <h3>
          <span class="section-icon">檔案上傳</span>
        </h3>
        
        <!-- 上傳區域 -->
        <div class="upload-area" 
             :class="{ 'dragging': isDragging }"
             @drop="handleDrop"
             @dragover="handleDragOver"
             @dragenter="handleDragEnter"
             @dragleave="handleDragLeave">
          
          <div class="upload-content">
            <div class="upload-icon">↑</div>
            <p class="upload-text">
              拖拽檔案到此處，或 
              <label class="upload-btn">
                <input 
                  type="file" 
                  ref="fileInput"
                  @change="handleFileSelect"
                  accept=".xlsx,.xls"
                  multiple
                  style="display: none;">
                點擊選擇檔案
              </label>
            </p>
            <p class="upload-hint">支援 .xlsx 和 .xls 格式</p>
          </div>
        </div>
        
        <!-- 上傳進度 -->
        <div v-if="uploadProgress.length > 0" class="upload-progress-section">
          <h4>上傳進度</h4>
          <div 
            v-for="progress in uploadProgress" 
            :key="progress.filename"
            class="progress-item">
            <div class="progress-info">
              <span class="filename">{{ progress.filename }}</span>
              <span class="progress-status" :class="progress.status">
                {{ getProgressStatusText(progress.status) }}
              </span>
            </div>
            <div class="progress-bar">
              <div 
                class="progress-fill" 
                :style="{ width: progress.percentage + '%' }"
                :class="progress.status">
              </div>
            </div>
          </div>
        </div>
        
        <!-- 檔案列表 -->
        <div class="file-list-section">
          <div class="file-list-header">
            <h4>data/ 資料夾檔案</h4>
            <button 
              class="btn btn-secondary btn-sm"
              @click="refreshFileList"
              :disabled="loadingFiles">
              <span v-if="loadingFiles" class="loading-icon">⟳</span>
              <span v-else>重新整理</span>
            </button>
          </div>
          
          <div v-if="dataFiles.length === 0" class="no-files">
            <span class="no-files-icon">資料夾</span>
            <p>data/ 資料夾中沒有檔案</p>
            <p class="hint">請上傳 Excel 檔案開始批次處理</p>
          </div>
          
          <div v-else class="file-grid">
            <div 
              v-for="file in dataFiles" 
              :key="file"
              class="file-card">
              <div class="file-info">
                <span class="file-icon">檔案</span>
                <span class="file-name">{{ file }}</span>
              </div>
              <button 
                class="file-delete-btn"
                @click="deleteFile(file)"
                :disabled="deletingFiles.includes(file)"
                title="刪除檔案">
                <span v-if="deletingFiles.includes(file)" class="loading-icon">⟳</span>
                <span v-else>刪除</span>
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 批次控制區域 -->
      <div class="batch-controls">
        <div class="control-group">
          <label for="productLine">選擇產品線：</label>
          <select 
            id="productLine" 
            v-model="selectedProductLine"
            :disabled="batchLoading"
            class="product-select">
            <option value="">請選擇產品線</option>
            <option 
              v-for="productLine in productLines" 
              :key="productLine" 
              :value="productLine">
              {{ productLine }}
            </option>
          </select>
        </div>
        
        <div class="control-buttons">
          <button 
            class="btn btn-primary batch-btn"
            @click="executeBatch"
            :disabled="batchLoading || !selectedProductLine">
            <span v-if="batchLoading" class="loading-icon">⟳</span>
            <span v-else class="btn-icon">執行</span>
            {{ batchLoading ? '處理中...' : '批次執行' }}
          </button>
          
          <button 
            class="btn btn-secondary"
            @click="checkHealth"
            :disabled="loading">
            <span class="btn-icon">檢查</span>
            健康檢查
          </button>
          
          <button 
            class="btn btn-info"
            @click="refreshProductLines"
            :disabled="loading">
            <span v-if="loading" class="loading-icon">⟳</span>
            <span v-else class="btn-icon">重新載入</span>
            {{ loading ? '載入中...' : '重新載入' }}
          </button>
        </div>
      </div>
      
      <!-- 系統狀態 -->
      <div class="system-status-panel">
        <div class="status-item">
          <span class="status-label">產品線數量：</span>
          <span class="status-value">{{ productLines.length }}</span>
        </div>
        <div class="status-item">
          <span class="status-label">檔案數量：</span>
          <span class="status-value">{{ dataFiles.length }}</span>
        </div>
        <div class="status-item">
          <span class="status-label">上次執行：</span>
          <span class="status-value">{{ lastExecutionTime || '無' }}</span>
        </div>
        <div class="status-item">
          <span class="status-label">系統版本：</span>
          <span class="status-value">v{{ version }}</span>
        </div>
      </div>
      
      <!-- 執行結果顯示區域 -->
      <div v-if="batchResult" class="result-section">
        <h3>
          <span class="result-icon">執行結果</span>
        </h3>
        
        <!-- 成功結果 -->
        <div v-if="batchResult.success" class="result-success">
          <div class="result-header">
            <span class="success-icon">成功</span>
            <strong>{{ batchResult.message }}</strong>
          </div>
          
          <!-- 詳細統計 -->
          <div v-if="hasDetailedStats" class="result-details">
            <h4>{{ batchResult.productType }} 處理詳情</h4>
            
            <div class="stats-grid">
              <!-- RMA 統計 -->
              <div v-if="batchResult.rmaStats" class="stat-card rma-stats">
                <div class="stat-header">
                  <span class="stat-icon">RMA</span>
                  <h5>RMA 資料</h5>
                </div>
                <div class="stat-number">{{ batchResult.rmaStats.total }}</div>
                <div class="stat-label">總處理筆數</div>
                <div class="stat-breakdown">
                  <span class="stat-item">
                    <span class="stat-tag new">新增</span>
                    {{ batchResult.rmaStats.inserted }} 筆
                  </span>
                  <span class="stat-item">
                    <span class="stat-tag update">更新</span>
                    {{ batchResult.rmaStats.updated }} 筆
                  </span>
                </div>
              </div>
              
              <!-- 庫存統計 -->
              <div v-if="batchResult.stockStats" class="stat-card stock-stats">
                <div class="stat-header">
                  <span class="stat-icon">庫存</span>
                  <h5>庫存資料</h5>
                </div>
                <div class="stat-number">{{ batchResult.stockStats.total }}</div>
                <div class="stat-label">總處理筆數</div>
                <div class="stat-breakdown">
                  <span class="stat-item">
                    <span class="stat-tag new">新增</span>
                    {{ batchResult.stockStats.inserted }} 筆
                  </span>
                  <span class="stat-item">
                    <span class="stat-tag update">更新</span>
                    {{ batchResult.stockStats.updated }} 筆
                  </span>
                </div>
              </div>
            </div>
            
            <!-- 總計 -->
            <div class="total-summary">
              <strong>總計處理筆數：{{ totalProcessedRecords }} 筆</strong>
              <small>執行時間：{{ executionDuration }}</small>
            </div>
          </div>
        </div>
        
        <!-- 失敗結果 -->
        <div v-else class="result-error">
          <div class="result-header">
            <span class="error-icon">失敗</span>
            <strong>執行失敗</strong>
          </div>
          <div class="error-message">{{ batchResult.message }}</div>
          
          <!-- 錯誤詳情 -->
          <div v-if="batchResult.details" class="error-details">
            <h4>錯誤詳情</h4>
            <ul>
              <li v-if="batchResult.details.rmaError">
                <strong>RMA 處理錯誤：</strong>{{ batchResult.details.rmaError }}
              </li>
              <li v-if="batchResult.details.stockError">
                <strong>庫存處理錯誤：</strong>{{ batchResult.details.stockError }}
              </li>
            </ul>
          </div>
        </div>
        
        <!-- 操作按鈕 -->
        <div class="result-actions">
          <button 
            class="btn btn-primary"
            @click="executeBatch"
            :disabled="batchLoading">
            重新執行
          </button>
          <button 
            class="btn btn-secondary"
            @click="clearResult">
            清除結果
          </button>
        </div>
      </div>
      
      <!-- 產品線管理區域 -->
      <div class="product-line-management">
        <h3>
          <span class="section-icon">產品線管理</span>
        </h3>
        
        <div class="management-controls">
          <button 
            class="btn btn-outline"
            @click="initProductLines"
            :disabled="loading">
            初始化產品線
          </button>
          
          <div class="product-line-list">
            <span class="list-label">目前產品線：</span>
            <div class="product-line-tags">
              <span 
                v-for="productLine in productLines" 
                :key="productLine"
                class="product-tag"
                :class="{ 'active': selectedProductLine === productLine }"
                @click="selectProductLine(productLine)">
                {{ productLine }}
              </span>
              <span v-if="productLines.length === 0" class="no-data">
                無產品線資料
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  
  data() {
    return {
      // 原有的資料
      productLines: [],
      selectedProductLine: '',
      loading: false,
      batchLoading: false,
      batchResult: null,
      lastExecutionTime: null,
      executionStartTime: null,
      version: '1.0.0',
      
      // 檔案上傳相關
      isDragging: false,
      uploadProgress: [],
      dataFiles: [],
      loadingFiles: false,
      deletingFiles: []
    };
  },
  
  computed: {
    // 是否有詳細統計資料
    hasDetailedStats() {
      return this.batchResult && 
             this.batchResult.success && 
             (this.batchResult.rmaStats || this.batchResult.stockStats);
    },
    
    // 總處理筆數
    totalProcessedRecords() {
      if (!this.hasDetailedStats) return 0;
      
      const rmaTotal = this.batchResult.rmaStats?.total || 0;
      const stockTotal = this.batchResult.stockStats?.total || 0;
      return rmaTotal + stockTotal;
    },
    
    // 執行時間
    executionDuration() {
      if (!this.executionStartTime || this.batchLoading) return '計算中...';
      
      const duration = Date.now() - this.executionStartTime;
      const seconds = Math.round(duration / 1000);
      return `${seconds} 秒`;
    }
  },
  
  async mounted() {
    await this.loadProductLines();
    await this.refreshFileList();
  },
  
  emits: ['show-message', 'loading'],
  
  methods: {
    // ==================== 原有方法 ====================
    
    // 載入產品線列表
    async loadProductLines() {
      try {
        this.loading = true;
        this.$emit('loading', true);
        
        const productLines = await api.batch.getProductLines();
        this.productLines = productLines;
        
        if (productLines.length === 0) {
          this.$emit('show-message', {
            type: 'warning',
            text: '沒有找到產品線，請先初始化產品線'
          });
        }
        
      } catch (error) {
        console.error('載入產品線失敗:', error);
        this.$emit('show-message', {
          type: 'error',
          text: '載入產品線失敗: ' + error.message
        });
      } finally {
        this.loading = false;
        this.$emit('loading', false);
      }
    },
    
    // 重新載入產品線
    async refreshProductLines() {
      await this.loadProductLines();
      this.$emit('show-message', {
        type: 'info',
        text: '產品線列表已重新載入'
      });
    },
    
    // 初始化產品線
    async initProductLines() {
      try {
        this.loading = true;
        this.$emit('loading', true);
        
        const result = await api.batch.initProductLines();
        
        this.$emit('show-message', {
          type: 'success',
          text: result.message
        });
        
        // 重新載入產品線列表
        await this.loadProductLines();
        
      } catch (error) {
        console.error('初始化產品線失敗:', error);
        this.$emit('show-message', {
          type: 'error',
          text: '初始化失敗: ' + error.message
        });
      } finally {
        this.loading = false;
        this.$emit('loading', false);
      }
    },
    
    // 執行批次處理
    async executeBatch() {
      if (!this.selectedProductLine) {
        this.$emit('show-message', {
          type: 'warning',
          text: '請選擇產品線'
        });
        return;
      }

      try {
        this.batchLoading = true;
        this.batchResult = null;
        this.executionStartTime = Date.now();
        this.$emit('loading', true);
        
        this.$emit('show-message', {
          type: 'info',
          text: `正在處理 ${this.selectedProductLine} 產品線資料，請稍候...`
        });
        
        // 執行批次處理
        const result = await api.batch.execute(this.selectedProductLine);
        this.batchResult = result;
        this.lastExecutionTime = new Date().toLocaleString('zh-TW');
        
        if (result.success) {
          this.$emit('show-message', {
            type: 'success',
            text: '批次處理完成！'
          });
        } else {
          this.$emit('show-message', {
            type: 'error',
            text: '批次處理失敗'
          });
        }
        
      } catch (error) {
        console.error('批次執行失敗:', error);
        this.batchResult = {
          success: false,
          message: error.message,
          productType: this.selectedProductLine
        };
        
        this.$emit('show-message', {
          type: 'error',
          text: '執行失敗: ' + error.message
        });
      } finally {
        this.batchLoading = false;
        this.$emit('loading', false);
      }
    },
    
    // 健康檢查
    async checkHealth() {
      try {
        this.loading = true;
        
        const health = await api.batch.health();
        
        this.$emit('show-message', {
          type: 'success',
          text: `服務正常運行，產品線數量: ${health.productLineCount}`
        });
        
      } catch (error) {
        console.error('健康檢查失敗:', error);
        this.$emit('show-message', {
          type: 'error',
          text: '服務檢查失敗: ' + error.message
        });
      } finally {
        this.loading = false;
      }
    },
    
    // 選擇產品線
    selectProductLine(productLine) {
      if (!this.batchLoading) {
        this.selectedProductLine = productLine;
      }
    },
    
    // 清除結果
    clearResult() {
      this.batchResult = null;
      this.lastExecutionTime = null;
      this.executionStartTime = null;
    },
    
    // ==================== 檔案上傳方法 ====================
    
    // 處理拖拽
    handleDragOver(e) {
      e.preventDefault();
    },
    
    handleDragEnter(e) {
      e.preventDefault();
      this.isDragging = true;
    },
    
    handleDragLeave(e) {
      e.preventDefault();
      if (e.target === e.currentTarget) {
        this.isDragging = false;
      }
    },
    
    handleDrop(e) {
      e.preventDefault();
      this.isDragging = false;
      
      const files = Array.from(e.dataTransfer.files);
      this.uploadFiles(files);
    },
    
    // 處理檔案選擇
    handleFileSelect(e) {
      const files = Array.from(e.target.files);
      this.uploadFiles(files);
      e.target.value = ''; // 清除選擇
    },
    
    // 上傳檔案
    async uploadFiles(files) {
      // 過濾只要 Excel 檔案
      const excelFiles = files.filter(file => 
        file.name.endsWith('.xlsx') || file.name.endsWith('.xls')
      );
      
      if (excelFiles.length === 0) {
        this.$emit('show-message', {
          type: 'warning',
          text: '請選擇 Excel 檔案（.xlsx 或 .xls）'
        });
        return;
      }
      
      // 為每個檔案建立進度追蹤
      excelFiles.forEach(file => {
        this.uploadProgress.push({
          filename: file.name,
          percentage: 0,
          status: 'uploading'
        });
      });
      
      // 逐個上傳檔案
      for (const file of excelFiles) {
        await this.uploadSingleFile(file);
      }
      
      // 上傳完成後重新整理檔案列表
      await this.refreshFileList();
    },
    
    // 上傳單個檔案
    async uploadSingleFile(file) {
      const progressItem = this.uploadProgress.find(p => p.filename === file.name);
      
      try {
        const formData = new FormData();
        formData.append('file', file);
        
        const response = await fetch('http://localhost:8080/api/upload/file', {
          method: 'POST',
          body: formData
        });
        
        const result = await response.json();
        
        if (result.success) {
          progressItem.percentage = 100;
          progressItem.status = 'success';
          
          this.$emit('show-message', {
            type: 'success',
            text: `檔案 ${file.name} 上傳成功`
          });
        } else {
          progressItem.status = 'error';
          this.$emit('show-message', {
            type: 'error',
            text: `檔案 ${file.name} 上傳失敗: ${result.message}`
          });
        }
        
      } catch (error) {
        progressItem.status = 'error';
        this.$emit('show-message', {
          type: 'error',
          text: `檔案 ${file.name} 上傳失敗: ${error.message}`
        });
      }
      
      // 2秒後移除進度項目
      setTimeout(() => {
        const index = this.uploadProgress.findIndex(p => p.filename === file.name);
        if (index !== -1) {
          this.uploadProgress.splice(index, 1);
        }
      }, 2000);
    },
    
    // 取得檔案列表
    async refreshFileList() {
      try {
        this.loadingFiles = true;
        
        const response = await fetch('http://localhost:8080/api/upload/files');
        const result = await response.json();
        
        if (result.success) {
          this.dataFiles = result.files || [];
        } else {
          this.$emit('show-message', {
            type: 'error',
            text: '取得檔案列表失敗: ' + result.message
          });
        }
        
      } catch (error) {
        this.$emit('show-message', {
          type: 'error',
          text: '取得檔案列表失敗: ' + error.message
        });
      } finally {
        this.loadingFiles = false;
      }
    },
    
    // 刪除檔案
    async deleteFile(filename) {
      if (!confirm(`確定要刪除檔案 "${filename}" 嗎？`)) {
        return;
      }
      
      try {
        this.deletingFiles.push(filename);
        
        const response = await fetch(`http://localhost:8080/api/upload/files/${filename}`, {
          method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.success) {
          this.$emit('show-message', {
            type: 'success',
            text: `檔案 ${filename} 刪除成功`
          });
          
          // 從列表中移除
          const index = this.dataFiles.indexOf(filename);
          if (index !== -1) {
            this.dataFiles.splice(index, 1);
          }
        } else {
          this.$emit('show-message', {
            type: 'error',
            text: '刪除失敗: ' + result.message
          });
        }
        
      } catch (error) {
        this.$emit('show-message', {
          type: 'error',
          text: '刪除失敗: ' + error.message
        });
      } finally {
        const index = this.deletingFiles.indexOf(filename);
        if (index !== -1) {
          this.deletingFiles.splice(index, 1);
        }
      }
    },
    
    // 取得進度狀態文字
    getProgressStatusText(status) {
      const statusMap = {
        'uploading': '上傳中...',
        'success': '成功',
        'error': '失敗'
      };
      return statusMap[status] || status;
    }
  }
};

// 註冊為全域組件
if (typeof window !== 'undefined' && window.Vue) {
  window.BatchExecution = BatchExecution;
}