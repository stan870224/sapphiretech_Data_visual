// FileUpload 頁面組件
const FileUpload = {
  template: `
    <div class="file-upload-page">
      <h2>檔案上傳</h2>
      
      <!-- 操作說明 -->
      <div class="operation-guide">
        <div class="guide-content">
          <h3>檔案上傳說明</h3>
          <p>1. 上傳 Excel 檔案到系統，用於批次處理</p>
          <p>2. 檔案命名格式：<code>{產品線}_RMA_record.xlsx</code> 和 <code>{產品線}_buffer_stock.xlsx</code></p>
          <p>3. 支援 .xlsx 和 .xls 格式</p>
          <p>4. 上傳完成後可在批次執行頁面進行處理</p>
        </div>
      </div>
      
      <!-- 檔案上傳區域 -->
      <div class="file-upload-section">
        <h3>檔案上傳</h3>
        
        <!-- 上傳區域 -->
        <div class="upload-area" 
             :class="{ 'dragging': isDragging }"
             @drop="handleDrop"
             @dragover="handleDragOver"
             @dragenter="handleDragEnter"
             @dragleave="handleDragLeave">
          
          <div class="upload-content">
            <div class="upload-icon">+</div>
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
              <span v-if="loadingFiles" class="loading-spinner"></span>
              <span v-else>重新整理</span>
            </button>
          </div>
          
          <div v-if="dataFiles.length === 0" class="no-files">
            <div class="no-files-icon">📁</div>
            <p>data/ 資料夾中沒有檔案</p>
            <p class="hint">請上傳 Excel 檔案開始批次處理</p>
          </div>
          
          <div v-else class="file-grid">
            <div 
              v-for="file in dataFiles" 
              :key="file"
              class="file-card">
              <div class="file-info">
                <span class="file-icon">📄</span>
                <span class="file-name">{{ file }}</span>
              </div>
              <button 
                class="file-delete-btn"
                @click="deleteFile(file)"
                :disabled="deletingFiles.includes(file)"
                title="刪除檔案">
                <span v-if="deletingFiles.includes(file)" class="loading-spinner"></span>
                <span v-else>刪除</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  
  data() {
    return {
      // 檔案上傳相關
      isDragging: false,
      uploadProgress: [],
      dataFiles: [],
      loadingFiles: false,
      deletingFiles: []
    };
  },
  
  async mounted() {
    await this.refreshFileList();
  },
  
  emits: ['show-message'],
  
  methods: {
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
        const result = await api.upload.uploadFile(file);
        
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
        
        const result = await api.upload.getFiles();
        
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
        
        const result = await api.upload.deleteFile(filename);
        
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
  window.FileUpload = FileUpload;
}