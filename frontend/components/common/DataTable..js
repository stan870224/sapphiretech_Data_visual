// DataTable 組件
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
    },
    selectable: {
      type: Boolean,
      default: false
    },
    sortable: {
      type: Boolean,
      default: true
    }
  },
  
  template: `
    <div class="data-table-wrapper">
      <div v-if="loading" class="table-loading">
        <loading-spinner text="載入中..."></loading-spinner>
      </div>
      
      <div v-else class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th v-if="selectable" class="select-column">
                <input 
                  type="checkbox" 
                  v-model="selectAll"
                  @change="toggleSelectAll">
              </th>
              <th 
                v-for="column in columns" 
                :key="column.key"
                :class="getHeaderClass(column)"
                @click="handleSort(column)">
                {{ column.title }}
                <span v-if="sortable && column.sortable !== false" class="sort-indicator">
                  <span :class="getSortClass(column.key, 'asc')">↑</span>
                  <span :class="getSortClass(column.key, 'desc')">↓</span>
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="sortedData.length === 0" class="no-data-row">
              <td :colspan="columnCount" class="no-data-cell">
                暫無資料
              </td>
            </tr>
            <tr 
              v-else
              v-for="(row, index) in sortedData" 
              :key="getRowKey(row, index)"
              class="data-row"
              :class="{ 'selected': isRowSelected(row) }"
              @click="handleRowClick(row)">
              <td v-if="selectable" class="select-column">
                <input 
                  type="checkbox" 
                  :value="getRowKey(row, index)"
                  v-model="selectedRows"
                  @click.stop>
              </td>
              <td 
                v-for="column in columns" 
                :key="column.key"
                :class="getCellClass(column)">
                <slot 
                  :name="column.key" 
                  :row="row" 
                  :value="getColumnValue(row, column.key)"
                  :index="index">
                  {{ formatCellValue(row, column) }}
                </slot>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="selectable && selectedRows.length > 0" class="selection-info">
        已選擇 {{ selectedRows.length }} 項
      </div>
    </div>
  `,
  
  data() {
    return {
      selectedRows: [],
      sortColumn: null,
      sortDirection: null
    };
  },
  
  computed: {
    selectAll: {
      get() {
        return this.data.length > 0 && this.selectedRows.length === this.data.length;
      },
      set(value) {
        if (value) {
          this.selectedRows = this.data.map((row, index) => this.getRowKey(row, index));
        } else {
          this.selectedRows = [];
        }
      }
    },
    
    columnCount() {
      return this.columns.length + (this.selectable ? 1 : 0);
    },
    
    sortedData() {
      if (!this.sortColumn || !this.sortDirection) {
        return this.data;
      }
      
      return [...this.data].sort((a, b) => {
        const aValue = this.getColumnValue(a, this.sortColumn);
        const bValue = this.getColumnValue(b, this.sortColumn);
        
        let result = 0;
        if (aValue < bValue) result = -1;
        else if (aValue > bValue) result = 1;
        
        return this.sortDirection === 'desc' ? -result : result;
      });
    }
  },
  
  emits: ['row-click', 'selection-change'],
  
  watch: {
    selectedRows(newVal) {
      this.$emit('selection-change', newVal);
    }
  },
  
  methods: {
    getRowKey(row, index) {
      return row.id || row.Serial_No || index;
    },
    
    getColumnValue(row, key) {
      return key.split('.').reduce((obj, key) => obj?.[key], row) || '';
    },
    
    formatCellValue(row, column) {
      const value = this.getColumnValue(row, column.key);
      
      if (column.formatter && typeof column.formatter === 'function') {
        return column.formatter(value, row);
      }
      
      if (column.type === 'date' && value) {
        try {
          return new Date(value).toLocaleDateString('zh-TW');
        } catch {
          return value;
        }
      }
      
      return value || '-';
    },
    
    getHeaderClass(column) {
      const classes = ['table-header'];
      if (column.align) classes.push(`text-${column.align}`);
      if (this.sortable && column.sortable !== false) classes.push('sortable');
      if (this.sortColumn === column.key) classes.push('sorted');
      return classes;
    },
    
    getCellClass(column) {
      const classes = ['table-cell'];
      if (column.align) classes.push(`text-${column.align}`);
      if (column.width) classes.push(`width-${column.width}`);
      return classes;
    },
    
    getSortClass(columnKey, direction) {
      return {
        'sort-arrow': true,
        'active': this.sortColumn === columnKey && this.sortDirection === direction
      };
    },
    
    handleSort(column) {
      if (!this.sortable || column.sortable === false) return;
      
      if (this.sortColumn === column.key) {
        if (this.sortDirection === 'asc') {
          this.sortDirection = 'desc';
        } else if (this.sortDirection === 'desc') {
          this.sortColumn = null;
          this.sortDirection = null;
        } else {
          this.sortDirection = 'asc';
        }
      } else {
        this.sortColumn = column.key;
        this.sortDirection = 'asc';
      }
    },
    
    handleRowClick(row) {
      this.$emit('row-click', row);
    },
    
    isRowSelected(row) {
      const key = this.getRowKey(row, this.data.indexOf(row));
      return this.selectedRows.includes(key);
    },
    
    toggleSelectAll() {
      // 由 computed selectAll setter 處理
    }
  }
};

// 註冊為全域組件
if (typeof window !== 'undefined' && window.Vue) {
  window.DataTable = DataTable;
}