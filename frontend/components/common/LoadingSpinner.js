// LoadingSpinner 組件
const LoadingSpinner = {
  props: {
    size: {
      type: String,
      default: 'medium',
      validator: value => ['small', 'medium', 'large'].includes(value)
    },
    color: {
      type: String,
      default: '#007bff'
    },
    text: {
      type: String,
      default: ''
    }
  },
  
  template: `
    <div class="loading-spinner-container" :class="containerClass">
      <div class="loading-spinner" :class="spinnerClass" :style="spinnerStyle"></div>
      <div v-if="text" class="loading-text">{{ text }}</div>
    </div>
  `,
  
  computed: {
    containerClass() {
      return `loading-${this.size}`;
    },
    
    spinnerClass() {
      return `spinner-${this.size}`;
    },
    
    spinnerStyle() {
      return {
        borderTopColor: this.color
      };
    }
  }
};

// 註冊為全域組件
if (typeof window !== 'undefined' && window.Vue) {
  window.LoadingSpinner = LoadingSpinner;
}