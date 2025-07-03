// Vue.js 主應用
const { createApp } = Vue;

// 全域狀態管理
const globalState = {
  productLines: [],
  currentPage: 'batch',
  loading: false,
  message: null
};

// 主 Vue 應用
const app = createApp({
  data() {
    return {
      ...globalState,
      productLines: [],
      currentPage: 'batch',
      loading: false,
      message: null
    };
  },

  async mounted() {
    await this.loadProductLines();
  },

  methods: {
    // 載入產品線列表
    async loadProductLines() {
      try {
        this.loading = true;
        const productLines = await api.batch.getProductLines();
        this.productLines = productLines;
      } catch (error) {
        this.showMessage('載入產品線失敗: ' + error.message, 'error');
      } finally {
        this.loading = false;
      }
    },

    // 切換頁面
    changePage(page) {
      this.currentPage = page;
      this.message = null; // 清除訊息
    },

    // 顯示訊息
    showMessage(text, type = 'info') {
      this.message = { text, type };
      
      // 3秒後自動隱藏
      setTimeout(() => {
        this.message = null;
      }, 3000);
    },

    // 檢查服務健康狀態
    async checkHealth() {
      try {
        const health = await api.batch.health();
        this.showMessage(`服務正常運行，產品線數量: ${health.productLineCount}`, 'success');
      } catch (error) {
        this.showMessage('服務檢查失敗: ' + error.message, 'error');
      }
    }
  },

  // 全域組件註冊
  components: {
    // Header 組件
    'app-header': {
      template: `
        <div class="header">
          <a href="#" class="logo">SAPPHIRE</a>
          <h1>Sapphire RMA Control</h1>
        </div>
      `
    },

    // Sidebar 組件
    'app-sidebar': {
      props: ['currentPage'],
      emits: ['page-change'],
      template: `
        <div class="sidebar">
          <button 
            :class="{ active: currentPage === 'select' }"
            @click="$emit('page-change', 'select')">
            資料查詢
          </button>
          <button 
            :class="{ active: currentPage === 'update' }"
            @click="$emit('page-change', 'update')">
            資料調整
          </button>
          <button 
            :class="{ active: currentPage === 'batch' }"
            @click="$emit('page-change', 'batch')">
            批次執行
          </button>
        </div>
      `
    },

    // 訊息組件
    'app-message': {
      props: ['message'],
      template: `
        <div v-if="message" :class="['message', message.type]">
          {{ message.text }}
        </div>
      `
    },

    // 載入指示器組件
    'loading-spinner': {
      template: `
        <div class="loading"></div>
      `
    }
  }
});

// 掛載應用
app.mount('#app');