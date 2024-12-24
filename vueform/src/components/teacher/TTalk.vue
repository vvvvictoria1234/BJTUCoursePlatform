<script>
export default {
  data() {
    return {
      commentDialogVisible: false, // 评论对话框显示状态
      commentListDialogVisible: false, // 评论列表对话框显示状态
      currentPostId: null, // 当前操作的帖子ID
      commentForm: {
        content: '',
        postId: null,
        userId: ''
      },
      commentRules: {
        content: [
          { required: true, message: '请输入评论内容', trigger: 'blur' }
        ]
      },
      commentsList: [], // 评论列表数据
      commentTotal: 0, // 评论总数
      commentPageSize: 10,
      commentPageNum: 1,
      userInfo: {
        id: '',
        name: '',
        phone: ''
      },
      tableData: [],
      pageSize: 10,
      pageNum: 1,
      total: 0,
      content: '',
      userId: '',
      centerDialogVisible: false,
      contentDialogVisible: false, // 查看内容的对话框
      currentContent: '', // 当前查看的帖子内容
      imageUrl: '', // 图片上传后的URL
      uploadHeaders: {
        // 如果需要token验证，在这里添加
        //'Authorization': localStorage.getItem('token')
      },
      form: {
        id: '',
        userId: '',
        content: '',
        imageUrl: ''
      },
      // 上传图片的限制
      uploadConfig: {
        maxSize: 100,
        acceptTypes: ['image/jpeg', 'image/png', 'image/gif']
      },
      rules: {
        userId: [
          { required: true, message: '请输入用户ID', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入内容', trigger: 'blur' }
        ]
      },
    }
  },
  methods: {
    // 处理图片上传前的校验
    beforeUpload(file) {
      const isValidType = this.uploadConfig.acceptTypes.includes(file.type);
      const isLessThan5M = file.size / 1024 / 1024 < this.uploadConfig.maxSize;

      if (!isValidType) {
        this.$message.error('只能上传 JPG/PNG/GIF 格式的图片!');
        return false;
      }
      if (!isLessThan5M) {
        this.$message.error(`图片大小不能超过 ${this.uploadConfig.maxSize}MB!`);
        return false;
      }
      return true;
    },


    // 处理图片上传失败
    handleUploadError() {
      this.$message.error('图片上传失败，请重试!');
    },

    // 移除图片
    handleRemove() {
      this.form.imageUrl = '';
      this.imageUrl = '';
    },

    // 预览图片
    handlePreview(file) {
      if (file.url) {
        this.previewUrl = file.url;
      } else {
        this.previewUrl = URL.createObjectURL(file.raw);
      }
      // 使用 Element UI 的 Image 组件预览
      this.$alert(`<img src="${this.previewUrl}" style="max-width: 100%;">`, '图片预览', {
        dangerouslyUseHTMLString: true,
        closeOnClickModal: true,
        showClose: true,
        showConfirmButton: false,
      });
    },
    // 打开评论对话框
    openCommentDialog(postId) {
      this.currentPostId = postId;
      this.commentForm.postId = postId;
      this.commentForm.userId = this.userInfo.id;
      this.commentDialogVisible = true;
    },

    // 打开评论列表对话框
    async viewComments(postId) {
      this.currentPostId = postId;
      this.commentPageNum = 1;
      this.commentListDialogVisible = true;
      await this.loadComments();
    },

    // 加载评论列表
    async loadComments() {
      try {
        const res = await this.$axios.post(this.$httpUrl + '/comments/list', {
          pageSize: this.commentPageSize,
          pageNum: this.commentPageNum,
          param: {
            postId: this.currentPostId
          }
        });

        if (res.data.code === 200) {
          this.commentsList = res.data.data;
          this.commentTotal = res.data.total;
        } else {
          this.$message.error('获取评论失败');
        }
      } catch (error) {
        console.error('加载评论失败:', error);
        this.$message.error('获取评论失败');
      }
    },

    // 提交评论
    submitComment() {
      this.$refs.commentForm.validate(async (valid) => {
        if (valid) {
          try {
            const res = await this.$axios.post(this.$httpUrl + '/comments/save', this.commentForm);

            if (res.data.code === 200) {
              this.$message.success('评论成功');
              this.commentDialogVisible = false;
              this.commentForm.content = '';
              // 如果评论列表对话框是打开的，刷新评论列表
              if (this.commentListDialogVisible) {
                await this.loadComments();
              }
            } else {
              this.$message.error(res.data.msg || '评论失败');
            }
          } catch (error) {
            console.error('提交评论失败:', error);
            this.$message.error('评论失败');
          }
        }
      });
    },

    // 处理评论分页大小变化
    handleCommentSizeChange(val) {
      this.commentPageSize = val;
      this.loadComments();
    },

    // 处理评论页码变化
    handleCommentCurrentChange(val) {
      this.commentPageNum = val;
      this.loadComments();
    },

    // 点赞评论
    async likeComment(commentId) {
      try {
        const res = await this.$axios.post(this.$httpUrl + '/comments/like', {
          id: commentId
        });

        if (res.data.code === 200) {
          this.$message.success('点赞成功');
          await this.loadComments();
        } else {
          this.$message.error(res.data.msg || '点赞失败');
        }
      } catch (error) {
        console.error('点赞评论失败:', error);
        this.$message.error('点赞失败');
      }
    },
    // 修改图片上传成功的处理方法
    handleUploadSuccess(res, file) {
      if (res.code === 200) {
        this.form.imageUrl = res.data; // 使用后端返回的URL
        this.$message.success('图片上传成功!');
      } else {
        this.$message.error(res.msg || '图片上传失败!');
      }
    },

    // 修改重置表单的方法
    resetForm() {
      if (this.$refs.form) {
        this.$refs.form.resetFields();
      }
      this.form = {
        id: '',
        userId: '',
        content: '',
        imageUrl: ''
      };
      // 清除上传的文件
      if (this.$refs.upload) {
        this.$refs.upload.clearFiles();
      }
    },

    // 查看帖子内容
    viewContent(row) {
      this.currentContent = row.content;
      this.contentDialogVisible = true;
    },
    likePost(id) {
      const url = `${this.$httpUrl}/posts/like`; // 使用后端定义的路径
      console.log('请求路径:', url, '请求参数:', {postId: id});

      this.$axios.post(this.$httpUrl + '/posts/like?id=' + id)
          .then(res => res.data)
          .then(res => {
            if (res.code === 200) {
              this.$message({
                message: '点赞成功！',
                type: 'success',
              });
              this.loadPost();
            } else {
              this.$message({
                message: '点赞失败！',
                type: 'error',
              });
            }
          })
          .catch(error => {
            console.error('请求错误:', error.response);
            this.$message({
              message: `请求失败: ${error.response?.data?.message || '未知错误'}`,
              type: 'error',
            });
          });
    },
    bookmarkPost(id) {
      const userId = this.userInfo.id;
      console.log('当前用户ID:', userId);
      if (!userId) {
        this.$message.warning('请先登录');
        return;
      }

      // 发送请求到新的收藏接口
      this.$axios.post(this.$httpUrl + `/api/bookmarks/${id}`, null, {
        params: {
          userId: userId
        }
      }).then(res => res.data).then(res => {
        if (res.code === 200) {
          this.$message({
            message: res.msg, // 使用后端返回的消息（"收藏成功"或"取消收藏成功"）
            type: 'success'
          });
          this.loadPost(); // 重新加载列表以更新收藏状态和数量
        } else {
          this.$message({
            message: res.msg || '操作失败',
            type: 'error'
          });
        }
      }).catch(error => {
        console.error('收藏操作失败:', error);
        this.$message({
          message: '操作失败，请重试',
          type: 'error'
        });
      });
    },


    del(id) {
      this.$axios.get(this.$httpUrl + '/posts/del?id=' + id).then(res => res.data).then(res => {
        if (res.code == 200) {
          this.$message({
            message: '删除成功！',
            type: 'success'
          });
          this.loadPost();
        } else {
          this.$message({
            message: '删除失败！',
            type: 'error'
          });
        }
      })
    },
    mod(row) {
      this.centerDialogVisible = true;
      this.$nextTick(() => {
        this.form.id = row.id;
        this.form.userId = row.userId;
        this.form.content = row.content;
        this.form.imageUrl = row.imageUrl;
      })
    },
    handleSizeChange(val) {
      this.pageNum = 1;
      this.pageSize = val;
      this.loadPost();
    },
    handleCurrentChange(val) {
      this.pageNum = val;
      this.loadPost();
    },
    resetParam() {
      this.content = '';
      this.userId = '';
    },
    add() {
      this.centerDialogVisible = true;
      this.$nextTick(() => {
        this.resetForm();
      })
    },
    save() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (this.form.id) {
            this.doMod();
          } else {
            this.doSave();
          }
        }
      });
    },
    doSave() {
      this.$axios.post(this.$httpUrl + '/posts/save', this.form).then(res => res.data).then(res => {
        if (res.code == 200) {
          this.$message({
            message: '发布成功！',
            type: 'success'
          });
          this.centerDialogVisible = false;
          this.loadPost();
          this.resetForm();
        } else {
          this.$message({
            message: '发布失败！',
            type: 'error'
          });
        }
      })
    },
    doMod() {
      this.$axios.post(this.$httpUrl + '/posts/update', this.form).then(res => res.data).then(res => {
        if (res.code == 200) {
          this.$message({
            message: '修改成功！',
            type: 'success'
          });
          this.centerDialogVisible = false;
          this.loadPost();
          this.resetForm();
        } else {
          this.$message({
            message: '修改失败！',
            type: 'error'
          });
        }
      })
    },
    //   loadPost() {
    //     this.$axios.post(this.$httpUrl + '/posts/listPageC1', {
    //       pageSize: this.pageSize,
    //       pageNum: this.pageNum,
    //       param: {
    //         content: this.content,
    //         userId: this.userId
    //       }
    //     }).then(res => res.data).then(res => {
    //       if(res.code == 200) {
    //         this.tableData = res.data;
    //         this.total = res.total;
    //       } else {
    //         this.$message.error('获取数据失败');
    //       }
    //     })
    //   }
    // },
    async loadPost() {
      const userId = this.userInfo.id;
      console.log('当前用户ID:', userId);

      try {
        console.log('开始请求帖子列表，参数:', {
          pageSize: this.pageSize,
          pageNum: this.pageNum,
          param: {
            content: this.content,
            userId: this.userId
          }
        });

        const res = await this.$axios.post(this.$httpUrl + '/posts/listPageC1', {
          pageSize: this.pageSize,
          pageNum: this.pageNum,
          param: {
            content: this.content,
            userId: this.userId
          }
        });

        console.log('帖子列表响应:', res.data);

        if (res.data.code === 200) {
          const posts = res.data.data;
          console.log('获取到的帖子列表:', posts);

          // 如果用户已登录，获取收藏状态
          if (userId) {
            const postIds = posts.map(post => post.id);
            console.log('准备获取收藏状态，帖子IDs:', postIds);

            try {
              // 获取所有帖子的收藏状态
              const bookmarkRes = await this.$axios.get(this.$httpUrl + '/api/bookmarks/status', {
                params: {
                  userId: userId,
                  postIds: postIds.join(',')
                }
              });

              console.log('收藏状态响应:', bookmarkRes.data);

              if (bookmarkRes.data.code === 200) {
                console.log('收藏状态数据:', bookmarkRes.data.data);

                // 更新每个帖子的收藏状态
                posts.forEach(post => {
                  const isBookmarked = bookmarkRes.data.data[post.id] || false;
                  console.log(`帖子${post.id}的收藏状态:`, isBookmarked);

                  this.$set(post, 'isBookmarked', isBookmarked);
                  this.$set(post, 'bookmarkLoading', false);
                });
              } else {
                console.warn('获取收藏状态失败:', bookmarkRes.data.msg);
              }
            } catch (bookmarkError) {
              console.error('获取收藏状态出错:', bookmarkError);
            }
          } else {
            console.log('用户未登录，跳过获取收藏状态');
          }

          console.log('最终处理的帖子数据:', posts);
          this.tableData = posts;
          this.total = res.data.total;
          console.log('更新后的表格数据:', this.tableData);
          console.log('总数:', this.total);
        } else {
          console.error('获取帖子列表失败:', res.data.msg);
          this.$message.error('获取数据失败');
        }
      } catch (error) {
        console.error('加载帖子失败，详细错误:', error);
        console.error('错误堆栈:', error.stack);
        this.$message.error('获取数据失败');
      }
    },
    // 从sessionStorage获取当前用户信息
    getCurrentUser() {
      const userStr = sessionStorage.getItem("CurUser")
      if (userStr) {
        const userData = JSON.parse(userStr)
        return userData.data || userData // 如果有data属性就返回data，否则返回整个对象
      }
      return null
    },
    async loadUserInfo() {
      try {
        // 先从sessionStorage获取用户信息
        const curUser = this.getCurrentUser()
        if (!curUser || !curUser.id) {
          this.$message.error('未获取到用户信息，请重新登录')
          return
        }

        // 使用用户ID查询最新信息
        const res = await this.$axios.post(this.$httpUrl + '/user/listPageC1', {
          pageSize: this.pageSize,
          pageNum: this.pageNum,
          param: {
            id: curUser.id
          }
        })

        if (res.data.code == 200 && res.data.data.length > 0) {
          this.userInfo = res.data.data[0]

          // 更新sessionStorage中的用户信息
          curUser.name = this.userInfo.name
          curUser.phone = this.userInfo.phone
          sessionStorage.setItem("CurUser", JSON.stringify(curUser))
        } else {
          // 如果接口查询失败，直接使用sessionStorage中的信息
          this.userInfo = curUser
        }
      } catch (error) {
        console.error('获取用户信息失败：', error)
        // 如果接口调用失败，使用sessionStorage中的信息
        const curUser = this.getCurrentUser()
        if (curUser) {
          this.userInfo = curUser
        } else {
          this.$message.error('获取用户信息失败，请重新登录')
        }
      }
    },
  },

  mounted() {
    this.loadPost();
  },
  created() {
    // 组件创建时，先尝试从sessionStorage加载用户信息
    const curUser = this.getCurrentUser()
    if (curUser) {
      this.userInfo = curUser
    }
    // 然后从后端获取最新信息
    this.loadUserInfo()
  }
};
</script>

<template>
  <div>
    <div style="margin-bottom: 5px;">
      <el-input v-model="content" placeholder="请输入内容关键词" suffix-icon="el-icon-search" style="width: 200px;"
                @keyup.enter.native="loadPost"></el-input>
      <el-input v-model="userId" placeholder="请输入用户ID" suffix-icon="el-icon-search" style="width: 200px; margin-left: 5px;"
                @keyup.enter.native="loadPost"></el-input>

      <el-button type="primary" style="margin-left: 5px;" @click="loadPost">查询</el-button>
      <el-button type="success" @click="resetParam">重置</el-button>
      <el-button type="primary" style="margin-left: 5px;" @click="add">发帖</el-button>
    </div>

    <el-table :data="tableData"
              :header-cell-style="{ background: '#f2f5fc', color: '#555555' }"
              border>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="userId" label="用户ID" width="120"></el-table-column>
      <el-table-column prop="content" label="内容" :show-overflow-tooltip="true"></el-table-column>
      <!-- 在表格中显示图片的列 -->
      <el-table-column prop="imageUrl" label="图片" width="100">
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
      <el-table-column prop="likesCount" label="点赞数" width="100"></el-table-column>
      <el-table-column prop="bookmarksCount" label="收藏数" width="100"></el-table-column>
      <el-table-column prop="createdAt" label="发布时间" width="180"></el-table-column>
      <el-table-column label="操作" width="600">
        <template slot-scope="scope">
          <el-button size="small" type="info" @click="viewContent(scope.row)">查看</el-button>
          <el-button size="small" type="primary" @click="likePost(scope.row.id)">
            <i class="el-icon-star-off"></i> 点赞
          </el-button>
          <el-button
              size="small"
              :type="scope.row.isBookmarked ? 'success' : 'warning'"
              @click="bookmarkPost(scope.row.id)"
              :loading="scope.row.bookmarkLoading">
            <i :class="scope.row.isBookmarked ? 'el-icon-star-on' : 'el-icon-collection'"></i>
            {{ scope.row.isBookmarked ? '已收藏' : '收藏' }}
          </el-button>
          <el-button size="small" type="success" @click="mod(scope.row)">编辑</el-button>
          <el-button size="small" type="primary" @click="openCommentDialog(scope.row.id)">
            <i class="el-icon-chat-line-round"></i> 评论
          </el-button>
          <el-button size="small" type="info" @click="viewComments(scope.row.id)">
            查看评论
          </el-button>
          <el-popconfirm
              title="确定删除吗？"
              @confirm="del(scope.row.id)"
              style="margin-left: 5px;"
          >
            <el-button slot="reference" size="small" type="danger">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[5, 10, 20, 30]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>

    <!-- 发帖/编辑对话框 -->
    <el-dialog
        title="帖子信息"
        :visible.sync="centerDialogVisible"
        width="40%"
        center>
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" v-model="form.content" rows="4"></el-input>
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
              :headers="uploadHeaders"
              :on-preview="handlePreview"
              :on-remove="handleRemove"
              :before-upload="beforeUpload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :limit="1"
              list-type="picture"
              ref="upload"
              class="upload-demo"
              action="http://localhost:8090/api/upload" >
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">
              只能上传 jpg/png/gif 文件，且不超过5MB
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </span>
    </el-dialog>


    <el-dialog
        title="发表评论"
        :visible.sync="commentDialogVisible"
        width="40%">
      <el-form ref="commentForm" :model="commentForm" :rules="commentRules" label-width="80px">
        <el-form-item label="评论内容" prop="content">
          <el-input
              type="textarea"
              :rows="4"
              placeholder="请输入评论内容"
              v-model="commentForm.content">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="commentDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="submitComment">发表评论</el-button>
  </span>
    </el-dialog>


    <el-dialog
        title="评论列表"
        :visible.sync="commentListDialogVisible"
        width="60%">
      <el-table
          :data="commentsList"
          :header-cell-style="{ background: '#f2f5fc', color: '#555555' }"
          border>
        <el-table-column prop="userId" label="用户ID" width="120"></el-table-column>
        <el-table-column prop="content" label="评论内容"></el-table-column>
        <el-table-column prop="likesCount" label="点赞数" width="100"></el-table-column>
        <el-table-column prop="createdAt" label="评论时间" width="180"></el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button size="small" type="primary" @click="likeComment(scope.row.id)">
              <i class="el-icon-star-off"></i> 点赞
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          @size-change="handleCommentSizeChange"
          @current-change="handleCommentCurrentChange"
          :current-page="commentPageNum"
          :page-sizes="[5, 10, 20, 30]"
          :page-size="commentPageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="commentTotal">
      </el-pagination>
    </el-dialog>

    <!-- 查看内容对话框 -->
    <el-dialog
        title="帖子内容"
        :visible.sync="contentDialogVisible"
        width="50%">
      <div style="white-space: pre-wrap;">{{ currentContent }}</div>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 添加必要的样式 */
.avatar-uploader {
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.el-upload__tip {
  line-height: 1.2;
  margin-top: 5px;
  color: #909399;
}

/* 图片预览样式 */
.upload-preview {
  margin-top: 10px;
}
</style>