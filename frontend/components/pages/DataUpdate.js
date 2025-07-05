// DataUpdate 頁面組件
const DataUpdate = {
  template: `
    <div class="data-update-page">
      <h2>資料調整</h2>
      
      <!-- 搜尋表單 -->
      <div class="query-form">
        <div class="form-group">
          <label>S/N:</label>
          <input type="text" v-model="searchForm.serialNo" placeholder="輸入序號">
        </div>
        <div class="form-group">
          <label>P/N:</label>
          <input type="text" v-model="searchForm.pn" placeholder="輸入零件號">
        </div>
        <div class="form-group">
          <label>SKU#:</label>
          <input type="text" v-model="searchForm.sku" placeholder="輸入SKU">
        </div>
        <div class="form-group">
          <label>產品線:</label>
          <select v-model="searchForm.productType">
            <option value="">請選擇產品線</option>
            <option 
              v-for="productLine in productLines" 
              :key="productLine" 
              :value="productLine">
              {{ productLine }}
            </option>
          </select>
        </div>
        <button class="button" @click="searchForUpdate" :disabled="loading || !searchForm.productType">
          <span v-if="loading" class="loading-spinner"></span>
          查詢
        </button>
      </div>

      <!-- 當前資料顯示區域 -->
      <div class="current-data">
        <h3>RMA 訂單資料</h3>
        <div class="current-data-content">
          <div class="current-data-item">
            <label>Rma No:</label>
            <input type="text" v-model="rmaData.rmaNo" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Customer Name:</label>
            <input type="text" v-model="rmaData.customerName" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Serial No:</label>
            <input type="text" v-model="rmaData.serialNo" :disabled="loading || mode === 'update'" />
          </div>
          <div class="current-data-item">
            <label>Part No:</label>
            <input type="text" v-model="rmaData.pn" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>SKU#:</label>
            <input type="text" v-model="rmaData.sku" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Product Name:</label>
            <input type="text" v-model="rmaData.productName" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Sell/Ship Date:</label>
            <input type="date" v-model="rmaData.sellShipDate" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Create Date:</label>
            <input type="date" v-model="rmaData.createDate" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Return Date:</label>
            <input type="date" v-model="rmaData.returnDate" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Failure desc:</label>
            <input type="text" v-model="rmaData.failureDesc" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>VI Damage Status:</label>
            <select v-model="rmaData.viDamageStatus" :disabled="loading">
              <option value="">請選擇</option>
              <option value="是">是</option>
              <option value="否">否</option>
            </select>
          </div>
          <div class="current-data-item">
            <label>Test Result Desc:</label>
            <input type="text" v-model="rmaData.testResultDesc" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Replacement SN in TW:</label>
            <input type="text" v-model="rmaData.replacementSnInTw" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Replacement PN in TW:</label>
            <input type="text" v-model="rmaData.replacementPnInTw" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Replacement SKU# in TW:</label>
            <input type="text" v-model="rmaData.replacementSkuInTw" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Replacement SN from HK:</label>
            <input type="text" v-model="rmaData.replacementSnFromHk" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Replacement PN from HK:</label>
            <input type="text" v-model="rmaData.replacementPnFromHk" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Replacement SKU# from HK:</label>
            <input type="text" v-model="rmaData.replacementSkuFromHk" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>RMA board Test Result:</label>
            <input type="text" v-model="rmaData.rmaBoardTestResult" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>End user invoice date:</label>
            <input type="date" v-model="rmaData.endUserInvoiceDate" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Warranty Until:</label>
            <input type="date" v-model="rmaData.warrantyUntil" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>Remark:</label>
            <input type="text" v-model="rmaData.remark" :disabled="loading" />
          </div>
          <div class="current-data-item">
            <label>產品線:</label>
            <select v-model="rmaData.productType" :disabled="loading">
              <option value="">請選擇產品線</option>
              <option 
                v-for="productLine in productLines" 
                :key="productLine" 
                :value="productLine">
                {{ productLine }}
              </option>
            </select>
          </div>
        </div>
        <button class="add-btn" @click="createRecord" :disabled="!canCreate || loading">
          <span v-if="loading && operationType === 'create'" class="loading-spinner"></span>
          新增
        </button>
        <button class="setup-btn" @click="updateRecord" :disabled="!canUpdate || loading">
          <span v-if="loading && operationType === 'update'" class="loading-spinner"></span>
          更新
        </button>
      </div>

      <!-- 可替換零件區域 -->
      <div class="replacement-section">
        <h3>庫存</h3>
        <table>
          <thead>
            <tr>
              <th width="8%">選擇</th>
              <th width="15%">Product name</th>
              <th width="15%">P/N</th>
              <th width="12%">SKU#</th>
              <th width="15%">S/N</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="stockData.length === 0">
              <td colspan="5" style="text-align: center; padding: 20px;">
                {{ searchForm.productType ? '沒有庫存資料' : '請先選擇產品線並查詢' }}
              </td>
            </tr>
            <tr v-for="stock in stockData" :key="stock.Serial_No">
              <td>
                <input 
                  type="radio" 
                  v-model="selectedStock" 
                  :value="stock.Serial_No"
                  name="stockSelect">
              </td>
              <td>{{ stock.Prodcut_name || '-' }}</td>
              <td>{{ stock.PN || '-' }}</td>
              <td>{{ stock.SKU || '-' }}</td>
              <td>{{ stock.Serial_No || '-' }}</td>
            </tr>
          </tbody>
        </table>
        <button class="update-btn" @click="replaceWithStock" :disabled="!selectedStock || loading">
          更換
        </button>
        <div style="clear: both;"></div>
      </div>
    </div>
  `,
  
  data() {
    return {
      loading: false,
      operationType: null, // 'create' or 'update'
      productLines: [],
      searchForm: {
        productType: '',
        serialNo: '',
        pn: '',
        sku: ''
      },
      rmaData: {
        productType: '',
        rmaNo: '',
        customerName: '',
        serialNo: '',
        pn: '',
        sku: '',
        productName: '',
        sellShipDate: '',
        createDate: '',
        returnDate: '',
        failureDesc: '',
        viDamageStatus: '',
        testResultDesc: '',
        replacementSnInTw: '',
        replacementPnInTw: '',
        replacementSkuInTw: '',
        replacementSnFromHk: '',
        replacementPnFromHk: '',
        replacementSkuFromHk: '',
        rmaBoardTestResult: '',
        endUserInvoiceDate: '',
        warrantyUntil: '',
        remark: ''
      },
      stockData: [],
      selectedStock: null,
      mode: 'create' // 'create' or 'update'
    };
  },
  
  computed: {
    canCreate() {
      return this.rmaData.serialNo && this.rmaData.productType && this.mode === 'create';
    },
    
    canUpdate() {
      return this.rmaData.serialNo && this.rmaData.productType && this.mode === 'update';
    }
  },
  
  async mounted() {
    await this.loadProductLines();
    
    // 檢查路由參數
    if (this.$route.query.productType && this.$route.query.serialNo) {
      this.searchForm.productType = this.$route.query.productType;
      this.searchForm.serialNo = this.$route.query.serialNo;
      await this.searchForUpdate();
    }
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
    
    async searchForUpdate() {
      if (!this.searchForm.productType) {
        this.$emit('show-message', {
          type: 'warning',
          text: '請選擇產品線'
        });
        return;
      }
      
      try {
        this.loading = true;
        
        const params = new URLSearchParams();
        params.append('productType', this.searchForm.productType);
        if (this.searchForm.serialNo) params.append('serialNo', this.searchForm.serialNo);
        if (this.searchForm.pn) params.append('pn', this.searchForm.pn);
        if (this.searchForm.sku) params.append('sku', this.searchForm.sku);
        
        const response = await fetch(`http://localhost:8080/api/rma/search-for-update?${params}`);
        const result = await response.json();
        
        if (result.success) {
          // 載入庫存資料
          this.stockData = result.stockRecords || [];
          
          // 如果找到 RMA 記錄，載入資料
          if (result.rmaRecord) {
            this.loadRmaData(result.rmaRecord);
            this.mode = 'update';
          } else {
            this.clearRmaData();
            this.mode = 'create';
          }
          
          this.$emit('show-message', {
            type: 'success',
            text: result.message
          });
        }
      } catch (error) {
        this.$emit('show-message', {
          type: 'error',
          text: '查詢失敗: ' + error.message
        });
      } finally {
        this.loading = false;
      }
    },
    
    loadRmaData(record) {
      this.rmaData = {
        productType: this.searchForm.productType,
        rmaNo: record.Rma_No || '',
        customerName: record.Customer_Name || '',
        serialNo: record.Serial_No || '',
        pn: record.PN || '',
        sku: record.SKU || '',
        productName: record.Product_Name || '',
        sellShipDate: this.formatDateForInput(record.Sell_Ship_Date),
        createDate: this.formatDateForInput(record.Create_Date),
        returnDate: this.formatDateForInput(record.Return_Date),
        failureDesc: record.Failure_desc || '',
        viDamageStatus: record.VI_Damage_Status || '',
        testResultDesc: record.Test_Result_Desc || '',
        replacementSnInTw: record.Replacement_SN_in_TW || '',
        replacementPnInTw: record.Replacement_PN_in_TW || '',
        replacementSkuInTw: record.Replacement_SKU_in_TW || '',
        replacementSnFromHk: record.Replacement_SN_from_HK || '',
        replacementPnFromHk: record.Replacement_PN_from_HK || '',
        replacementSkuFromHk: record.Replacement_SKU_from_HK || '',
        rmaBoardTestResult: record.RMA_board_Test_Result || '',
        endUserInvoiceDate: this.formatDateForInput(record.End_user_invoice_date),
        warrantyUntil: this.formatDateForInput(record.Warranty_Until),
        remark: record.Remark || ''
      };
    },
    
    clearRmaData() {
      this.rmaData = {
        productType: this.searchForm.productType,
        rmaNo: '',
        customerName: '',
        serialNo: '',
        pn: '',
        sku: '',
        productName: '',
        sellShipDate: '',
        createDate: '',
        returnDate: '',
        failureDesc: '',
        viDamageStatus: '',
        testResultDesc: '',
        replacementSnInTw: '',
        replacementPnInTw: '',
        replacementSkuInTw: '',
        replacementSnFromHk: '',
        replacementPnFromHk: '',
        replacementSkuFromHk: '',
        rmaBoardTestResult: '',
        endUserInvoiceDate: '',
        warrantyUntil: '',
        remark: ''
      };
    },
    
    replaceWithStock() {
      if (!this.selectedStock) return;
      
      const stock = this.stockData.find(s => s.Serial_No === this.selectedStock);
      if (stock) {
        // 填入 TW 替換資料
        this.rmaData.replacementSnInTw = stock.Serial_No;
        this.rmaData.replacementPnInTw = stock.PN;
        this.rmaData.replacementSkuInTw = stock.SKU;
        
        this.$emit('show-message', {
          type: 'info',
          text: '已填入替換資料，請點擊更新按鈕保存'
        });
      }
    },
    
    async createRecord() {
      try {
        this.loading = true;
        this.operationType = 'create';
        
        const response = await fetch('http://localhost:8080/api/rma/create', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.rmaData)
        });
        
        const result = await response.json();
        
        if (result.success) {
          this.$emit('show-message', {
            type: 'success',
            text: result.message
          });
          this.mode = 'update';
        } else {
          this.$emit('show-message', {
            type: 'error',
            text: result.message
          });
        }
      } catch (error) {
        this.$emit('show-message', {
          type: 'error',
          text: '新增失敗: ' + error.message
        });
      } finally {
        this.loading = false;
        this.operationType = null;
      }
    },
    
    async updateRecord() {
      try {
        this.loading = true;
        this.operationType = 'update';
        
        const updateData = {
          ...this.rmaData,
          stockSerialNoToDelete: this.selectedStock
        };
        
        const response = await fetch('http://localhost:8080/api/rma/update', {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(updateData)
        });
        
        const result = await response.json();
        
        if (result.success) {
          this.$emit('show-message', {
            type: 'success',
            text: result.message
          });
          
          // 如果有刪除庫存，重新載入庫存列表
          if (this.selectedStock) {
            this.stockData = this.stockData.filter(s => s.Serial_No !== this.selectedStock);
            this.selectedStock = null;
          }
        } else {
          this.$emit('show-message', {
            type: 'error',
            text: result.message
          });
        }
      } catch (error) {
        this.$emit('show-message', {
          type: 'error',
          text: '更新失敗: ' + error.message
        });
      } finally {
        this.loading = false;
        this.operationType = null;
      }
    },
    
    formatDateForInput(dateString) {
      if (!dateString) return '';
      try {
        const date = new Date(dateString);
        return date.toISOString().split('T')[0];
      } catch {
        return '';
      }
    }
  }
};

if (typeof window !== 'undefined' && window.Vue) {
  window.DataUpdate = DataUpdate;
}