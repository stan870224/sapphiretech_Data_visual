// DataQuery 頁面組件
const DataQuery = {
  template: `
    <div class="data-query-page">
      <h2>資料查詢</h2>
      
      <!-- 查詢欄位 -->
      <div class="query-form">
        <div class="form-group">
          <label for="sn">S/N<br>(可用掃碼):</label>
          <input type="text" id="sn" v-model="searchForm.serialNo">
        </div>
        <div class="form-group">
          <label for="pn">P/N<br>(可用掃碼):</label>
          <input type="text" id="pn" v-model="searchForm.pn">
        </div>
        <div class="form-group">
          <label for="sku">SKU#<br>(可用掃碼):</label>
          <input type="text" id="sku" v-model="searchForm.sku">
        </div>
        <div class="form-group">
          <label for="product-line">產品線:</label>
          <select id="product-line" v-model="searchForm.productType">
            <option value="">請選擇產品線</option>
            <option 
              v-for="productLine in productLines" 
              :key="productLine" 
              :value="productLine">
              {{ productLine }}
            </option>
          </select>
        </div>

        <!-- 換行 -->
        <div class="form-group label-block">
          <label for="start-date">資料起迄:</label>
          <input type="date" id="start-date" v-model="searchForm.startDate">
          <span style="margin: 0 10px;">~</span>
          <input type="date" id="end-date" v-model="searchForm.endDate">
        </div>

        <button class="button" @click="searchData" :disabled="loading || !searchForm.productType">
          <span v-if="loading" class="loading-spinner"></span>
          查詢
        </button>
      </div>

      <!-- RMA 表格 -->
      <h3>RMA Record</h3>
      <div v-if="searchResults && searchResults.success" class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th title="RMA Number">RMA No</th>
              <th title="Customer Name">Customer Name</th>
              <th title="Serial No">Serial No</th>
              <th title="Part No">Part No</th>
              <th title="SKU#">SKU#</th>
              <th title="Product Name">Product Name</th>
              <th title="Sell/Ship Date">Sell/Ship Date</th>
              <th title="Create Date">Create Date</th>
              <th title="Return Date">Return Date</th>
              <th title="Failure Description">Failure Desc</th>
              <th title="VI Damage Status">VI Damage Status</th>
              <th title="Test Result Description">Test Result Desc</th>
              <th title="Replacement SN in TW">Replacement SN in TW</th>
              <th title="Replacement PN in TW">Replacement PN in TW</th>
              <th title="Replacement SKU# in TW">Replacement SKU# in TW</th>
              <th title="Replacement SN from HK">Replacement SN from HK</th>
              <th title="Replacement PN from HK">Replacement PN from HK</th>
              <th title="Replacement SKU# from HK">Replacement SKU# from HK</th>
              <th title="RMA board Test Result">RMA board Test Result</th>
              <th title="End user invoice date">End user invoice date</th>
              <th title="Warranty Until">Warranty Until</th>
              <th title="Remark">Remark</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="searchResults.rmaRecords.length === 0">
              <td colspan="22" style="text-align: center; padding: 20px;">
                沒有找到符合條件的記錄
              </td>
            </tr>
            <tr v-for="record in searchResults.rmaRecords" :key="record.Serial_No">
              <td>{{ record.Rma_No || '-' }}</td>
              <td>{{ record.Customer_Name || '-' }}</td>
              <td>{{ record.Serial_No || '-' }}</td>
              <td>{{ record.PN || '-' }}</td>
              <td>{{ record.SKU || '-' }}</td>
              <td>{{ record.Product_Name || '-' }}</td>
              <td>{{ formatDate(record.Sell_Ship_Date) }}</td>
              <td>{{ formatDate(record.Create_Date) }}</td>
              <td>{{ formatDate(record.Return_Date) }}</td>
              <td>{{ record.Failure_desc || '-' }}</td>
              <td>{{ record.VI_Damage_Status || '-' }}</td>
              <td>{{ record.Test_Result_Desc || '-' }}</td>
              <td>{{ record.Replacement_SN_in_TW || '-' }}</td>
              <td>{{ record.Replacement_PN_in_TW || '-' }}</td>
              <td>{{ record.Replacement_SKU_in_TW || '-' }}</td>
              <td>{{ record.Replacement_SN_from_HK || '-' }}</td>
              <td>{{ record.Replacement_PN_from_HK || '-' }}</td>
              <td>{{ record.Replacement_SKU_from_HK || '-' }}</td>
              <td>{{ record.RMA_board_Test_Result || '-' }}</td>
              <td>{{ formatDate(record.End_user_invoice_date) }}</td>
              <td>{{ formatDate(record.Warranty_Until) }}</td>
              <td>{{ record.Remark || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  
  data() {
    return {
      loading: false,
      productLines: [],
      searchForm: {
        productType: '',
        serialNo: '',
        pn: '',
        sku: '',
        startDate: '',
        endDate: ''
      },
      searchResults: null
    };
  },
  
  async mounted() {
    await this.loadProductLines();
  },
  
  emits: ['show-message'],
  
  methods: {
    async loadProductLines() {
      try {
        const productLines = await api.rma.getProductLines();
        this.productLines = productLines;
      } catch (error) {
        this.$emit('show-message', {
          type: 'error',
          text: '載入產品線失敗: ' + error.message
        });
      }
    },
    
    async searchData() {
      if (!this.searchForm.productType) {
        this.$emit('show-message', {
          type: 'warning',
          text: '請選擇產品線'
        });
        return;
      }

      try {
        this.loading = true;
        
        const searchParams = {
          productType: this.searchForm.productType,
          serialNo: this.searchForm.serialNo || null,
          pn: this.searchForm.pn || null,
          sku: this.searchForm.sku || null,
          startDate: this.searchForm.startDate || null,
          endDate: this.searchForm.endDate || null
        };
        
        const result = await api.rma.search(searchParams);
        this.searchResults = result;
        
        if (result.success) {
          this.$emit('show-message', {
            type: 'success',
            text: `找到 ${result.totalCount} 筆記錄`
          });
        } else {
          this.$emit('show-message', {
            type: 'error',
            text: result.message
          });
        }
      } catch (error) {
        this.$emit('show-message', {
          type: 'error',
          text: '搜尋失敗: ' + error.message
        });
      } finally {
        this.loading = false;
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return '-';
      try {
        return new Date(dateString).toLocaleDateString('zh-TW');
      } catch {
        return '-';
      }
    }
  }
};

if (typeof window !== 'undefined' && window.Vue) {
  window.DataQuery = DataQuery;
}