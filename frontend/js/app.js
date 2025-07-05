// Vue.js 主應用
const { createApp } = Vue;
const { createRouter, createWebHashHistory } = VueRouter;

// 路由配置
const routes = [
  {
    path: '/',
    redirect: '/batch-execution' // 預設導向批次執行頁面
  },
  {
    path: '/data-query',
    name: 'DataQuery',
    component: () => DataQuery, // 資料查詢頁面
    meta: {
      title: '資料查詢',
      icon: '🔍'
    }
  },
  {
    path: '/data-update',
    name: 'DataUpdate', 
    component: () => DataUpdate, // 資料調整頁面
    meta: {
      title: '資料調整',
      icon: '✏️'
    }
  },
  {
    path: '/batch-execution',
    name: 'BatchExecution',
    component: () => BatchExecution, // 批次執行頁面
    meta: {
      title: '批次執行',
      icon: '⚡'
    }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/batch-execution' // 404 重導向
  }
];

// 建立路由器
const router = createRouter({
  history: createWebHashHistory(), // 使用 hash 模式，不需要伺服器配置
  routes
});

// 路由守衛
router.beforeEach((to, from, next) => {
  // 更新頁面標題
  if (to.meta && to.meta.title) {
    document.title = `${to.meta.title} - Sapphire RMA Control`;
  } else {
    document.title = 'Sapphire RMA Control';
  }
  
  // 載入進度指示
  const app = document.querySelector('#app').__vue_app__;
  if (app) {
    app.config.globalProperties.$isRouteChanging = true;
  }
  
  next();
});

router.afterEach((to, from) => {
  // 路由切換完成
  setTimeout(() => {
    const app = document.querySelector('#app').__vue_app__;
    if (app) {
      app.config.globalProperties.$isRouteChanging = false;
    }
  }, 100);
});

// 主應用組件
const App = {
  template: `
    <div class="app-wrapper">
      <!-- 載入中指示器 -->
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>系統載入中...</p>
      </div>
      
      <!-- 主要應用內容 -->
      <div v-else class="app-container">
        <div class="container">
          <!-- 頁首組件 -->
          <app-header 
            @show-message="showGlobalMessage"
            @system-refresh="handleSystemRefresh">
          </app-header>

          <!-- 主體區 -->
          <div class="main">
            <!-- 側邊欄組件 -->
            <app-sidebar 
              :current-route="$route.path"
              @navigate="handleNavigation"
              @show-message="showGlobalMessage">
            </app-sidebar>

            <!-- 主顯示區 -->
            <div class="content">
              <!-- 路由切換載入指示 -->
              <div v-if="isRouteChanging" class="route-loading">
                <div class="route-loading-bar"></div>
              </div>
              
              <!-- 路由視圖 -->
              <router-view 
                :key="$route.fullPath"
                @show-message="showGlobalMessage"
                @loading="setLoading"
                v-slot="{ Component }">
                <transition name="page-fade" mode="out-in">
                  <component :is="Component" />
                </transition>
              </router-view>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 全域訊息容器 -->
      <div class="alert-container">
        <message-alert 
          :message="globalMessage"
          @close="clearGlobalMessage">
        </message-alert>
      </div>
    </div>
  `,
  
  data() {
    return {
      loading: true,
      isRouteChanging: false,
      globalMessage: null,
      systemInfo: {
        version: '1.0.0',
        buildTime: new Date().toLocaleString('zh-TW')
      }
    };
  },
  
  async mounted() {
    try {
      // 初始化應用
      await this.initializeApp();
      
      // 設定全域錯誤處理
      this.setupErrorHandling();
      
      // 載入完成
      this.loading = false;
      
      // 顯示歡迎訊息
      this.showGlobalMessage({
        type: 'success',
        text: '歡迎使用 Sapphire RMA 系統'
      });
      
    } catch (error) {
      console.error('應用初始化失敗:', error);
      this.showGlobalMessage({
        type: 'error',
        text: '系統初始化失敗，請重新整理頁面'
      });
      this.loading = false;
    }
  },
  
  methods: {
    // 初始化應用
    async initializeApp() {
      try {
        // 檢查必要的服務
        await this.checkServices();
        
        // 載入系統設定
        await this.loadSystemSettings();
        
        console.log('Sapphire RMA 系統初始化完成');
        
      } catch (error) {
        console.error('初始化過程中發生錯誤:', error);
        throw error;
      }
    },
    
    // 檢查服務狀態
    async checkServices() {
      try {
        // 檢查 API 服務是否可用
        if (typeof api !== 'undefined') {
          const health = await api.batch.health();
          console.log('後端服務狀態:', health.status);
        } else {
          console.warn('API 服務尚未載入');
        }
      } catch (error) {
        console.warn('後端服務檢查失敗:', error.message);
        // 不阻斷初始化，只是警告
      }
    },
    
    // 載入系統設定
    async loadSystemSettings() {
      try {
        // 這裡可以載入使用者設定、主題等
        const savedTheme = localStorage.getItem('rma-theme') || 'light';
        document.documentElement.setAttribute('data-theme', savedTheme);
        
      } catch (error) {
        console.warn('載入系統設定失敗:', error);
      }
    },
    
    // 設定全域錯誤處理
    setupErrorHandling() {
      // Vue 錯誤處理
      this.$app.config.errorHandler = (error, instance, info) => {
        console.error('Vue 錯誤:', error);
        console.error('錯誤資訊:', info);
        
        this.showGlobalMessage({
          type: 'error',
          text: '應用發生錯誤，請聯繫技術支援'
        });
      };
      
      // 全域 JavaScript 錯誤處理
      window.addEventListener('error', (event) => {
        console.error('JavaScript 錯誤:', event.error);
        this.showGlobalMessage({
          type: 'error',
          text: '系統發生未預期錯誤'
        });
      });
      
      // Promise 錯誤處理
      window.addEventListener('unhandledrejection', (event) => {
        console.error('未處理的 Promise 錯誤:', event.reason);
        this.showGlobalMessage({
          type: 'error',
          text: 'API 呼叫發生錯誤'
        });
      });
    },
    
    // 顯示全域訊息
    showGlobalMessage(message) {
      this.globalMessage = {
        ...message,
        timestamp: Date.now()
      };
    },
    
    // 清除全域訊息
    clearGlobalMessage() {
      this.globalMessage = null;
    },
    
    // 處理導航
    handleNavigation(routePath) {
      this.isRouteChanging = true;
      
      // 清除當前訊息
      this.clearGlobalMessage();
      
      // 導航到新路由
      if (this.$route.path !== routePath) {
        this.$router.push(routePath);
      }
    },
    
    // 處理系統重新整理
    async handleSystemRefresh() {
      try {
        this.isRouteChanging = true;
        
        // 重新檢查服務
        await this.checkServices();
        
        // 重新載入當前頁面數據
        this.$emit('system-refresh');
        
        this.showGlobalMessage({
          type: 'success',
          text: '系統已重新整理'
        });
        
      } catch (error) {
        this.showGlobalMessage({
          type: 'error',
          text: '系統重新整理失敗: ' + error.message
        });
      } finally {
        this.isRouteChanging = false;
      }
    },
    
    // 設定載入狀態
    setLoading(loading) {
      this.isRouteChanging = loading;
    }
  },
  
  // 監聽路由變化
  watch: {
    '$route'(to, from) {
      // 路由變化時的處理
      console.log(`路由變化: ${from.path} → ${to.path}`);
      
      // 清除訊息
      setTimeout(() => {
        this.clearGlobalMessage();
      }, 100);
    }
  }
};

// 建立並掛載應用
const app = createApp(App);

// 使用路由
app.use(router);

// 註冊全域組件
app.component('AppHeader', AppHeader);
app.component('AppSidebar', AppSidebar);
app.component('MessageAlert', MessageAlert);

// 全域屬性
app.config.globalProperties.$api = typeof api !== 'undefined' ? api : null;
app.config.globalProperties.$version = '1.0.0';

// 掛載應用
app.mount('#app');

// 開發模式下的額外配置
if (typeof window !== 'undefined') {
  window.app = app;
  window.router = router;
  
  // 開發工具
  if (process?.env?.NODE_ENV === 'development') {
    console.log('Sapphire RMA 系統已啟動');
    console.log('Vue 應用:', app);
    console.log('路由器:', router);
  }
}