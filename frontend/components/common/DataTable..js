// DataTable 組件 - 簡化版本
const DataTable = {
  props: {
    columns: {
      type: Array,
      required: true
    },
    data: {
      type: Array,
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  
  template: `
    <div class="data-table-wrapper">
      <div v-if="loading" class="table-loading">
        <loading-spinner></loading-spinner>
      </div>
      
      <div v-else class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th 
                v-for="column in columns" 
                :key="column.key">
                {{ column.title }}
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="data.length === 0" class="no-data-row">
              <td :colspan="columns.length" class="no-data-cell">
                暫無資料
              </td>
            </tr>
            <tr 
              v-else
              v-for="(row, index) in data" 
              :key="index"
              class="data-row"
              @click="$emit('row-click', row)">
              <td 
                v-for="column in columns" 
                :key="column.key">
                {{ getColumnValue(row, column.key) || '-' }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  
  emits: ['row-click'],
  
  methods: {
    getColumnValue(row, key) {
      return key.split('.').reduce((obj, key) => obj?.[key], row) || '';
    }
  }
};

// 註冊為全域組件
if (typeof window !== 'undefined' && window.Vue) {
  window.DataTable = DataTable;
}