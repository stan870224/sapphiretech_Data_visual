// Home 首頁組件
const Home = {
  template: `
    <div class="home-page">
      <!-- 歡迎橫幅 -->
      <div class="welcome-banner">
        <div class="banner-content">
          <h1 class="system-title">
            <span class="logo-text">SAPPHIRE</span>
            <span class="subtitle">RMA Control System</span>
          </h1>
          <p class="system-description">
            專業的 RMA 管理系統，提供完整的維修記錄追蹤與庫存管理功能
          </p>
          <div class="version-badge">
            Version {{ version }}
          </div>
        </div>
        <div class="banner-illustration">
          <div class="feature-icon">🏠</div>
        </div>
      </div>
    </div>
  `,
  
  data() {
    return {
      version: '1.0.0'
    };
  },
  
  emits: ['show-message']
};

// 註冊為全域組件
if (typeof window !== 'undefined' && window.Vue) {
  window.Home = Home;
}

// 註冊為全域組件
if (typeof window !== 'undefined' && window.Vue) {
  window.Home = Home;
}