const { createApp } = Vue;
const { createRouter, createWebHistory } = VueRouter;

// Axios封装
const api = axios.create({
    baseURL: '/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器
api.interceptors.request.use(
    config => {
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 响应拦截器
api.interceptors.response.use(
    response => {
        const res = response.data;
        if (res.code === 200) {
            return res;
        } else {
            ElementPlus.ElMessage.error(res.message || '请求失败');
            return Promise.reject(new Error(res.message || '请求失败'));
        }
    },
    error => {
        ElementPlus.ElMessage.error(error.message || '网络错误');
        return Promise.reject(error);
    }
);

// 产品管理页面组件
const ProductsPage = {
    template: '#products-page',
    data() {
        return {
            tableData: [],
            total: 0,
            pageNum: 1,
            pageSize: 10,
            dialogVisible: false,
            dialogTitle: '新增产品',
            form: {
                productId: null,
                productName: '',
                category: '',
                brand: '',
                description: '',
                basePrice: null,
                status: '上架'
            },
            searchForm: {
                productName: '',
                category: '',
                brand: '',
                status: ''
            },
            rules: {
                productName: [{ required: true, message: '产品名称不能为空', trigger: 'blur' }],
                basePrice: [{ required: true, message: '基础价格不能为空', trigger: 'blur' }]
            }
        };
    },
    mounted() {
        this.loadData();
    },
    methods: {
        loadData() {
            const params = {
                pageNum: this.pageNum,
                pageSize: this.pageSize,
                ...this.searchForm
            };
            api.get('/products', { params }).then(res => {
                this.tableData = res.data.list;
                this.total = res.data.total;
            });
        },
        handleSearch() {
            this.pageNum = 1;
            this.loadData();
        },
        handleReset() {
            this.searchForm = {
                productName: '',
                category: '',
                brand: '',
                status: ''
            };
            this.handleSearch();
        },
        handleAdd() {
            this.dialogTitle = '新增产品';
            this.$nextTick(() => {
                this.form = {
                    productId: null,
                    productName: '',
                    category: '',
                    brand: '',
                    description: '',
                    basePrice: null,
                    status: '上架'
                };
                this.dialogVisible = true;
                if (this.$refs.form) {
                    this.$refs.form.clearValidate();
                }
            });
        },
        handleEdit(row) {
            this.dialogTitle = '编辑产品';
            this.$nextTick(() => {
                this.form = {
                    productId: row.productId,
                    productName: row.productName || '',
                    category: row.category || '',
                    brand: row.brand || '',
                    description: row.description || '',
                    basePrice: row.basePrice || null,
                    status: row.status || '上架'
                };
                this.dialogVisible = true;
                if (this.$refs.form) {
                    this.$refs.form.clearValidate();
                }
            });
        },
        handleDelete(row) {
            this.$confirm('确定要删除该产品吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                api.delete(`/products/${row.productId}`).then(() => {
                    ElementPlus.ElMessage.success('删除成功');
                    this.loadData();
                });
            });
        },
        handleSave() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    const url = this.form.productId ? `/products/${this.form.productId}` : '/products';
                    const method = this.form.productId ? 'put' : 'post';
                    api[method](url, this.form).then(() => {
                        ElementPlus.ElMessage.success('保存成功');
                        this.dialogVisible = false;
                        this.loadData();
                    });
                }
            });
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.loadData();
        },
        handleCurrentChange(val) {
            this.pageNum = val;
            this.loadData();
        }
    }
};

// 仓库管理页面组件
const WarehousesPage = {
    template: '#warehouses-page',
    data() {
        return {
            tableData: [],
            total: 0,
            pageNum: 1,
            pageSize: 10,
            dialogVisible: false,
            dialogTitle: '新增仓库',
            form: {
                warehouseId: null,
                warehouseName: '',
                location: '',
                manager: '',
                capacity: null,
                status: 1
            },
            searchForm: {
                warehouseName: '',
                status: null
            },
            rules: {
                warehouseName: [{ required: true, message: '仓库名称不能为空', trigger: 'blur' }]
            }
        };
    },
    mounted() {
        this.loadData();
    },
    methods: {
        loadData() {
            const params = {
                pageNum: this.pageNum,
                pageSize: this.pageSize,
                ...this.searchForm
            };
            api.get('/warehouses', { params }).then(res => {
                this.tableData = res.data.list;
                this.total = res.data.total;
            });
        },
        handleSearch() {
            this.pageNum = 1;
            this.loadData();
        },
        handleReset() {
            this.searchForm = {
                warehouseName: '',
                status: null
            };
            this.handleSearch();
        },
        handleAdd() {
            this.dialogTitle = '新增仓库';
            this.$nextTick(() => {
                this.form = {
                    warehouseId: null,
                    warehouseName: '',
                    location: '',
                    manager: '',
                    capacity: null,
                    status: 1
                };
                this.dialogVisible = true;
                if (this.$refs.form) {
                    this.$refs.form.clearValidate();
                }
            });
        },
        handleEdit(row) {
            this.dialogTitle = '编辑仓库';
            this.$nextTick(() => {
                this.form = {
                    warehouseId: row.warehouseId,
                    warehouseName: row.warehouseName || '',
                    location: row.location || '',
                    manager: row.manager || '',
                    capacity: row.capacity || null,
                    status: row.status !== undefined ? row.status : 1
                };
                this.dialogVisible = true;
                if (this.$refs.form) {
                    this.$refs.form.clearValidate();
                }
            });
        },
        handleDelete(row) {
            this.$confirm('确定要删除该仓库吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                api.delete(`/warehouses/${row.warehouseId}`).then(() => {
                    ElementPlus.ElMessage.success('删除成功');
                    this.loadData();
                });
            });
        },
        handleSave() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    const url = this.form.warehouseId ? `/warehouses/${this.form.warehouseId}` : '/warehouses';
                    const method = this.form.warehouseId ? 'put' : 'post';
                    api[method](url, this.form).then(() => {
                        ElementPlus.ElMessage.success('保存成功');
                        this.dialogVisible = false;
                        this.loadData();
                    });
                }
            });
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.loadData();
        },
        handleCurrentChange(val) {
            this.pageNum = val;
            this.loadData();
        }
    }
};

// 库存管理页面组件
const InventoryPage = {
    template: '#inventory-page',
    data() {
        return {
            tableData: [],
            total: 0,
            pageNum: 1,
            pageSize: 10,
            dialogVisible: false,
            dialogTitle: '新增库存',
            form: {
                inventoryId: null,
                warehouseId: null,
                skuId: null,
                quantity: 0,
                allocatedQty: 0
            },
            searchForm: {
                warehouseId: null,
                productName: ''
            },
            warehouseList: [],
            rules: {
                warehouseId: [{ required: true, message: '仓库不能为空', trigger: 'change' }],
                skuId: [{ required: true, message: 'SKU不能为空', trigger: 'change' }],
                quantity: [{ required: true, message: '数量不能为空', trigger: 'blur' }]
            }
        };
    },
    mounted() {
        this.loadWarehouses();
        this.loadData();
    },
    methods: {
        loadWarehouses() {
            api.get('/warehouses', { params: { pageNum: 1, pageSize: 1000 } }).then(res => {
                this.warehouseList = res.data.list;
            });
        },
        loadData() {
            const params = {
                pageNum: this.pageNum,
                pageSize: this.pageSize,
                ...this.searchForm
            };
            api.get('/inventory', { params }).then(res => {
                this.tableData = res.data.list;
                this.total = res.data.total;
            });
        },
        handleSearch() {
            this.pageNum = 1;
            this.loadData();
        },
        handleReset() {
            this.searchForm = {
                warehouseId: null,
                productName: ''
            };
            this.handleSearch();
        },
        handleAdd() {
            this.dialogTitle = '新增库存';
            this.$nextTick(() => {
                this.form = {
                    inventoryId: null,
                    warehouseId: null,
                    skuId: null,
                    quantity: 0,
                    allocatedQty: 0
                };
                this.dialogVisible = true;
                if (this.$refs.form) {
                    this.$refs.form.clearValidate();
                }
            });
        },
        handleEdit(row) {
            this.dialogTitle = '编辑库存';
            this.$nextTick(() => {
                this.form = {
                    inventoryId: row.inventoryId,
                    warehouseId: row.warehouseId || null,
                    skuId: row.skuId || null,
                    quantity: row.quantity || 0,
                    allocatedQty: row.allocatedQty || 0
                };
                this.dialogVisible = true;
                if (this.$refs.form) {
                    this.$refs.form.clearValidate();
                }
            });
        },
        handleDelete(row) {
            this.$confirm('确定要删除该库存记录吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                api.delete(`/inventory/${row.inventoryId}`).then(() => {
                    ElementPlus.ElMessage.success('删除成功');
                    this.loadData();
                });
            });
        },
        handleSave() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    const url = this.form.inventoryId ? `/inventory/${this.form.inventoryId}` : '/inventory';
                    const method = this.form.inventoryId ? 'put' : 'post';
                    api[method](url, this.form).then(() => {
                        ElementPlus.ElMessage.success('保存成功');
                        this.dialogVisible = false;
                        this.loadData();
                    });
                }
            });
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.loadData();
        },
        handleCurrentChange(val) {
            this.pageNum = val;
            this.loadData();
        }
    }
};

// 路由配置
const routes = [
    {
        path: '/',
        redirect: '/products'
    },
    {
        path: '/products',
        component: ProductsPage
    },
    {
        path: '/warehouses',
        component: WarehousesPage
    },
    {
        path: '/inventory',
        component: InventoryPage
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

// App组件
const App = {
    template: `
        <el-container style="height: 100vh;">
            <el-header class="app-header" style="color: white; display: flex; align-items: center; padding: 0 24px;">
                <h1 style="margin: 0; font-size: 24px; font-weight: 500; letter-spacing: 2px;">衣库通</h1>
            </el-header>
            <el-container>
                <el-aside class="app-sidebar" width="220px" style="background-color: #304156;">
                    <el-menu
                        :default-active="$route.path"
                        router
                        background-color="#304156"
                        text-color="#bfcbd9"
                        active-text-color="#409EFF"
                    >
                        <el-menu-item index="/products">
                            <span>产品管理</span>
                        </el-menu-item>
                        <el-menu-item index="/warehouses">
                            <span>仓库管理</span>
                        </el-menu-item>
                        <el-menu-item index="/inventory">
                            <span>库存管理</span>
                        </el-menu-item>
                    </el-menu>
                </el-aside>
                <el-main style="background-color: #f5f7fa; padding: 0;">
                    <router-view />
                </el-main>
            </el-container>
        </el-container>
    `
};

// 创建应用
const app = createApp(App);
app.use(router);
app.use(ElementPlus);
app.mount('#app');
