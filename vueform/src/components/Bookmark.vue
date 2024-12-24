<template>
  <div>
    <div style="margin-bottom: 5px;">
      <el-input
          v-model="content"
          placeholder="请输入帖子内容关键词"
          suffix-icon="el-icon-search"
          style="width: 200px;"
          @keyup.enter.native="loadPost">
      </el-input>

      <el-button
          type="primary"
          style="margin-left: 5px;"
          @click="loadPost">
        查询
      </el-button>
      <el-button
          type="success"
          @click="resetParam">
        重置
      </el-button>
    </div>

    <!-- 收藏列表表格 -->
    <el-table
        :data="tableData"
        :header-cell-style="{ background: '#f2f5fc', color: '#555555' }"
        v-loading="loading"
        border>
      <el-table-column
          prop="id"
          label="帖子ID"
          width="80">
      </el-table-column>

      <el-table-column
          prop="userId"
          label="发帖人ID"
          width="120">
      </el-table-column>

      <el-table-column
          prop="content"
          label="帖子内容"
          :show-overflow-tooltip="true">
      </el-table-column>

      <el-table-column
          prop="imageUrl"
          label="帖子图片"
          width="100">
        <template slot-scope="scope">
          <el-image
              v-if="scope.row.imageUrl"
              style="width: 50px; height: 50px"
              :src="scope.row.imageUrl"
              :preview-src-list="[scope.row.imageUrl]">
          </el-image>
          <span v-else>无图片</span>
        </template>
      </el-table-column>

      <el-table-column
          label="操作"
          width="180"
          align="center">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="info"
              @click="viewContent(scope.row)">
            查看
          </el-button>
          <el-button
              size="mini"
              type="danger"
              @click="handleRemoveBookmark(scope.row)">
            取消收藏
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页器 -->
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[5, 10, 20, 30]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top: 20px;">
    </el-pagination>

    <!-- 查看内容对话框 -->
    <el-dialog
        title="帖子内容"
        :visible.sync="contentDialogVisible"
        width="50%">
      <div v-if="selectedPost">
        <div style="white-space: pre-wrap;">{{ selectedPost.content }}</div>
        <el-image
            v-if="selectedPost.imageUrl"
            style="width: 100%; max-height: 400px; margin-top: 20px;"
            :src="selectedPost.imageUrl"
            :preview-src-list="[selectedPost.imageUrl]">
        </el-image>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'MyBookmarks',
  data() {
    return {
      userInfo: {
        id: '',
        name: '',
        phone: ''
      },
      tableData: [],
      loading: false,
      content: '',
      pageNum: 1,
      pageSize: 10,
      total: 0,
      contentDialogVisible: false,
      selectedPost: null
    }
  },
  created() {
    // 组件创建时，先尝试从sessionStorage加载用户信息
    const curUser = this.getCurrentUser()
    if (curUser) {
      this.userInfo = curUser
    }
    this.loadUserInfo()
  },
  mounted() {
    this.loadPost() // 页面加载时获取收藏的帖子
  },
  methods: {
    // 加载收藏列表
    async loadPost() {
      this.loading = true;
      try {
        const userId = this.userInfo.id;
        console.log('当前用户ID:', userId);

        const res = await this.$axios.post(this.$httpUrl + '/api/bookmarks/my', {
          pageSize: this.pageSize,
          pageNum: this.pageNum,
          param: {
            userId: userId,
            content: this.content
          }
        });

        console.log('收藏列表响应:', res.data);

        if (res.data.code === 200) {
          this.tableData = res.data.data;
          this.total = res.data.total;
        } else {
          this.$message.error(res.data.msg || '获取收藏列表失败');
        }
      } catch (error) {
        console.error('加载收藏列表失败:', error);
        this.$message.error('获取收藏列表失败');
      } finally {
        this.loading = false;
      }
    },

    // 取消收藏
    async handleRemoveBookmark(row) {
      try {
        const userId = this.userInfo.id;
        const res = await this.$axios.post(this.$httpUrl + `/api/bookmarks/${row.id}`, null, {
          params: { userId }
        });

        if (res.data.code === 200) {
          this.$message.success('取消收藏成功');
          this.loadPost(); // 重新加载列表
        } else {
          this.$message.error(res.data.msg || '取消收藏失败');
        }
      } catch (error) {
        console.error('取消收藏失败:', error);
        this.$message.error('取消收藏失败');
      }
    },

    // 从sessionStorage获取当前用户信息
    getCurrentUser() {
      const userStr = sessionStorage.getItem("CurUser")
      if (userStr) {
        const userData = JSON.parse(userStr)
        return userData.data || userData
      }
      return null
    },

    async loadUserInfo() {
      try {
        const curUser = this.getCurrentUser()
        if (!curUser || !curUser.id) {
          this.$message.error('未获取到用户信息，请重新登录')
          return
        }

        const res = await this.$axios.post(this.$httpUrl + '/user/listPageC1', {
          pageSize: this.pageSize,
          pageNum: this.pageNum,
          param: {
            id: curUser.id
          }
        })

        if (res.data.code == 200 && res.data.data.length > 0) {
          this.userInfo = res.data.data[0]
          curUser.name = this.userInfo.name
          curUser.phone = this.userInfo.phone
          sessionStorage.setItem("CurUser", JSON.stringify(curUser))
        } else {
          this.userInfo = curUser
        }
      } catch (error) {
        console.error('获取用户信息失败：', error)
        const curUser = this.getCurrentUser()
        if (curUser) {
          this.userInfo = curUser
        } else {
          this.$message.error('获取用户信息失败，请重新登录')
        }
      }
    },

    // 查看帖子内容
    viewContent(row) {
      this.selectedPost = row;
      this.contentDialogVisible = true;
    },

    // 重置查询条件
    resetParam() {
      this.content = '';
      this.pageNum = 1;
      this.loadPost(); // 改为调用 loadPost
    },

    // 分页相关方法
    handleSizeChange(val) {
      this.pageSize = val;
      this.pageNum = 1;
      this.loadPost(); // 改为调用 loadPost
    },

    handleCurrentChange(val) {
      this.pageNum = val;
      this.loadPost(); // 改为调用 loadPost
    }
  }
}
</script>

<style scoped>
.el-table {
  margin-top: 20px;
}
.el-button + .el-button {
  margin-left: 5px;
}
.el-pagination {
  text-align: center;
  margin-top: 20px;
}
</style>