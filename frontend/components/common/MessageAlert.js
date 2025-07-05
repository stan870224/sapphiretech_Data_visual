// MessageAlert 組件
const MessageAlert = {
  props: {
    message: {
      type: Object,
      default: null,
      validator(value) {
        // 驗證 message 物件格式
        if (!value) return true;
        return value.hasOwnProperty('type') && value.hasOwnProperty('text');
      }
    },
    autoHide: {
      type: Boolean,
      default: true
    },
    duration: {
      type: Number,
      default: 3000 // 3秒
    }
  },
  
  template: `
    <transition name="alert-fade">
      <div 
        v-if="isVisible && message" 
        :class="alertClasses"
        class="message-alert"
        role="alert"
        :aria-live="message.type === 'error' ? 'assertive' : 'polite'">
        
        <!-- 訊息圖示 -->
        <div class="alert-icon">
          <span>{{ alertIcon }}</span>
        </div>
        
        <!-- 訊息內容 -->
        <div class="alert-content">
          <div class="alert-text" v-html="formattedText"></div>
          
          <!-- 如果有標題 -->
          <div v-if="message.title" class="alert-title">
            {{ message.title }}
          </div>
        </div>
        
        <!-- 關閉按鈕 -->
        <button 
          class="alert-close" 
          @click="closeAlert"
          :aria-label="'關閉' + message.type + '訊息'">
          ✕
        </button>
        
        <!-- 進度條（如果自動隱藏） -->
        <div 
          v-if="autoHide && showProgressBar" 
          class="alert-progress"
          :style="{ width: progressWidth + '%' }">
        </div>
      </div>
    </transition>
  `,
  
  data() {
    return {
      isVisible: false,
      hideTimer: null,
      progressTimer: null,
      progressWidth: 100,
      showProgressBar: false
    };
  },
  
  computed: {
    // 訊息樣式類別
    alertClasses() {
      if (!this.message) return '';
      
      return [
        'alert',
        `alert-${this.message.type}`,
        {
          'alert-with-title': this.message.title,
          'alert-with-progress': this.autoHide && this.showProgressBar
        }
      ];
    },
    
    // 訊息圖示
    alertIcon() {
      if (!this.message) return '';
      
      const icons = {
        success: '✓',
        error: '✗',
        warning: '⚠',
        info: 'ℹ'
      };
      
      return icons[this.message.type] || 'ℹ';
    },
    
    // 格式化文字（支援換行）
    formattedText() {
      if (!this.message || !this.message.text) return '';
      
      // 將 \n 轉換為 <br>
      return this.message.text.replace(/\n/g, '<br>');
    }
  },
  
  emits: ['close'],
  
  watch: {
    // 監聽 message 變化
    message: {
      handler(newMessage) {
        if (newMessage) {
          this.showAlert();
        } else {
          this.hideAlert();
        }
      },
      immediate: true
    }
  },
  
  methods: {
    // 顯示警告
    showAlert() {
      this.isVisible = true;
      this.progressWidth = 100;
      
      // 清除之前的計時器
      this.clearTimers();
      
      // 如果自動隱藏
      if (this.autoHide && this.duration > 0) {
        this.showProgressBar = true;
        this.startProgressBar();
        this.startHideTimer();
      }
    },
    
    // 隱藏警告
    hideAlert() {
      this.isVisible = false;
      this.clearTimers();
    },
    
    // 關閉警告
    closeAlert() {
      this.hideAlert();
      this.$emit('close');
    },
    
    // 開始進度條動畫
    startProgressBar() {
      const interval = 50; // 更新間隔 (ms)
      const step = (interval / this.duration) * 100; // 每次減少的百分比
      
      this.progressTimer = setInterval(() => {
        this.progressWidth -= step;
        if (this.progressWidth <= 0) {
          this.progressWidth = 0;
          clearInterval(this.progressTimer);
        }
      }, interval);
    },
    
    // 開始隱藏計時器
    startHideTimer() {
      this.hideTimer = setTimeout(() => {
        this.closeAlert();
      }, this.duration);
    },
    
    // 清除所有計時器
    clearTimers() {
      if (this.hideTimer) {
        clearTimeout(this.hideTimer);
        this.hideTimer = null;
      }
      
      if (this.progressTimer) {
        clearInterval(this.progressTimer);
        this.progressTimer = null;
      }
    },
    
    // 滑鼠進入時暫停自動隱藏
    pauseAutoHide() {
      if (this.autoHide) {
        this.clearTimers();
      }
    },
    
    // 滑鼠離開時恢復自動隱藏
    resumeAutoHide() {
      if (this.autoHide && this.isVisible) {
        const remainingTime = (this.progressWidth / 100) * this.duration;
        if (remainingTime > 0) {
          this.startProgressBar();
          this.hideTimer = setTimeout(() => {
            this.closeAlert();
          }, remainingTime);
        }
      }
    }
  },
  
  beforeUnmount() {
    this.clearTimers();
  }
};

// 註冊為全域組件
if (typeof window !== 'undefined' && window.Vue) {
  window.MessageAlert = MessageAlert;
}