// Header 組件
const AppHeader = {
  template: `
    <div class="header">
      <a href="#" @click="goHome" class="logo">SAPPHIRE</a>
      <h1>Sapphire RMA Control</h1>
      
      <!-- 右側工具列 -->
      <div class="header-tools">
        <!-- 系統狀態指示器 -->
        <div class="system-status" :class="{ 'online': isSystemOnline, 'offline': !isSystemOnline }">
          <span class="status-dot"></span>
          <span class="status-text">{{ systemStatusText }}</span>
        </div>
        
        <!-- 重新整理按鈕 -->
        <button 
          class="refresh-btn" 
          @click="refreshSystem"
          :disabled="isRefreshing"
          title="重新整理系統狀態">
          <span v-if="isRefreshing" class="loading-icon">⟳</span>
          <span v-else>↻</span>
        </button>
        
        <!-- 系統時間 -->
        <div class="system-time">
          {{ currentTime }}
        </div>
      </div>
    </div>
  `,
  
  data() {
    return {
      isSystemOnline: true,
      isRefreshing: false,
      currentTime: '',
      timeInterval: null
    };
  },
  
  computed: {
    systemStatusText() {
      return this.isSystemOnline ? '系統正常' : '系統離線';
    }
  },
  
  mounted() {
    this.updateTime();
    this.startTimeUpdate();
    this.checkSystemStatus();
  },
  
  beforeUnmount() {
    if (this.timeInterval) {
      clearInterval(this.timeInterval);
    }
  },
  
  methods: {
    // 返回首頁
    goHome() {
      this.$router.push('/');
    },
    
    // 更新當前時間
    updateTime() {
      const now = new Date();
      this.currentTime = now.toLocaleString('zh-TW', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      });
    },
    
    // 開始時間更新
    startTimeUpdate() {
      this.timeInterval = setInterval(() => {
        this.updateTime();
      }, 1000);
    },
    
    // 檢查系統狀態
    async checkSystemStatus() {
      try {
        // 檢查後端 API 是否正常
        const response = await api.batch.health();
        this.isSystemOnline = response.status === 'healthy';
      } catch (error) {
        console.warn('系統健康檢查失敗:', error);
        this.isSystemOnline = false;
      }
    },
    
    // 重新整理系統
    async refreshSystem() {
      this.isRefreshing = true;
      
      try {
        // 檢查系統狀態
        await this.checkSystemStatus();
        
        // 觸發全域事件，通知其他組件重新載入
        this.$emit('system-refresh');
        
        // 顯示成功訊息
        this.$emit('show-message', {
          type: 'success',
          text: '系統狀態已重新整理'
        });
        
      } catch (error) {
        this.$emit('show-message', {
          type: 'error',
          text: '重新整理失敗: ' + error.message
        });
      } finally {
        this.isRefreshing = false;
      }
    }
  }
};

// 註冊為全域組件
if (typeof window !== 'undefined' && window.Vue) {
  window.AppHeader = AppHeader;
}