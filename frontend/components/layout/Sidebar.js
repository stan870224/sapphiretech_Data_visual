// Sidebar 組件
const AppSidebar = {
  props: {
    currentRoute: {
      type: String,
      default: ''
    }
  },
  
  template: `
    <div class="sidebar">
      <!-- 導航按鈕 -->
      <nav class="sidebar-nav">
        <router-link 
          to="/data-query" 
          class="nav-button"
          :class="{ 'active': isActiveRoute('/data-query') }"
          @click="handleNavigation('/data-query')">
          <span class="nav-icon">🔍</span>
          <span class="nav-text">資料查詢</span>
        </router-link>
        
        <router-link 
          to="/data-update" 
          class="nav-button"
          :class="{ 'active': isActiveRoute('/data-update') }"
          @click="handleNavigation('/data-update')">
          <span class="nav-icon">✏️</span>
          <span class="nav-text">資料調整</span>
        </router-link>
        
        <router-link 
          to="/batch-execution" 
          class="nav-button"
          :class="{ 'active': isActiveRoute('/batch-execution') }"
          @click="handleNavigation('/batch-execution')">
          <span class="nav-icon">⚡</span>
          <span class="nav-text">批次執行</span>
        </router-link>
      </nav>
      
      <!-- 分隔線 -->
      <div class="sidebar-divider"></div>
      
      <!-- 系統功能 -->
      <div class="sidebar-footer">
        <button 
          class="system-button"
          @click="openSettings"
          title="系統設定">
          <span class="nav-icon">⚙️</span>
          <span class="nav-text">設定</span>
        </button>
        
        <button 
          class="system-button"
          @click="showHelp"
          title="使用說明">
          <span class="nav-icon">❓</span>
          <span class="nav-text">說明</span>
        </button>
      </div>
      
      <!-- 版本資訊 -->
      <div class="version-info">
        <small>v{{ version }}</small>
      </div>
    </div>
  `,
  
  data() {
    return {
      version: '1.0.0'
    };
  },
  
  emits: ['navigate', 'show-message'],
  
  methods: {
    // 檢查當前路由是否啟用
    isActiveRoute(routePath) {
      return this.$route.path === routePath || this.currentRoute === routePath;
    },
    
    // 處理導航
    handleNavigation(routePath) {
      // 觸發導航事件
      this.$emit('navigate', routePath);
      
      // 使用 Vue Router 導航
      if (this.$router && this.$route.path !== routePath) {
        this.$router.push(routePath);
      }
    },
    
    // 開啟設定
    openSettings() {
      this.$emit('show-message', {
        type: 'info',
        text: '設定功能開發中...'
      });
    },
    
    // 顯示說明
    showHelp() {
      this.$emit('show-message', {
        type: 'info',
        text: '使用說明：\n1. 資料查詢：查詢 RMA 記錄\n2. 資料調整：新增、修改 RMA 記錄\n3. 批次執行：批次處理 Excel 檔案'
      });
    }
  },
  
  // 監聽路由變化
  watch: {
    '$route'(to, from) {
      // 路由變化時可以做一些處理
      console.log('路由從', from.path, '變更到', to.path);
    }
  }
};

// 註冊為全域組件
if (typeof window !== 'undefined' && window.Vue) {
  window.AppSidebar = AppSidebar;
}