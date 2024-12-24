
<script>
export default {
  data() {
    return {
      noticeDialog: {
        visible: false,
        courseId: '',
        form: {
          title: '',
          content: '',
          courseId: '',
          publisherId: ''
        }
      },
      // 通知表单验证规则
      noticeRules: {
        title: [
          { required: true, message: '请输入通知标题', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入通知内容', trigger: 'blur' }
        ]
      },
      resourceListDialog: {
        visible: false,
        resourceList: []
      },
      // 课程列表数据
      courseList: [],
      // 分页参数
      pageSize: 10,
      pageNum: 1,
      total: 0,
      // 查询参数
      searchForm: {
        courseName: '',
      },
      // 课程详情对话框
      courseDialog: {
        visible: false,
        title: '课程信息'
      },
      // 课程表单
      courseForm: {
        courseId: '',
        courseName: '',
        courseDescription: '',
        calendar: '',
        syllabus: '',
        teacherId: ''
      },
      // 表单验证规则
      rules: {
        courseName: [
          {required: true, message: '请输入课程名称', trigger: 'blur'}
        ],
        courseDescription: [
          {required: true, message: '请输入课程描述', trigger: 'blur'}
        ]
      },
      // 学生列表对话框
      studentDialog: {
        visible: false,
        courseId: '',
        studentList: []
      },
      // 资源上传对话框
      resourceDialog: {
        visible: false,
        courseId: '',
        fileList: []
      },
      // 当前登录教师信息
      teacherInfo: {
        id: '',
        name: '',
        phone: ''
      },
      // 文件上传配置
      uploadHeaders: {},
      uploadConfig: {
        maxSize: 100,
        acceptTypes: [
          'application/pdf', // PDF
          'application/msword', // DOC
          'application/vnd.openxmlformats-officedocument.wordprocessingml.document', // DOCX
          'application/vnd.ms-powerpoint', // PPT
          'application/vnd.openxmlformats-officedocument.presentationml.presentation', // PPTX
          'text/markdown', // MD
          'text/x-markdown' // 另一种 MD MIME 类型
        ],
        acceptExts: '.pdf,.doc,.docx,.ppt,.pptx,.md' // 用于el-upload的accept属性
      }
    }
  },
  methods: {
    // 打开发送通知对话框
    openNoticeDialog(courseId) {
      this.noticeDialog.courseId = courseId;
      this.noticeDialog.form = {
        title: '',
        content: '',
        courseId: courseId,
        publisherId: this.teacherInfo.id
      };
      this.noticeDialog.visible = true;
    },

    // 发送通知
    async sendNotice() {
      try {
        // 表单验证
        const valid = await this.$refs.noticeForm.validate();
        if (!valid) return;

        const res = await this.$axios.post(
            this.$httpUrl + '/course/notice/publish',
            this.noticeDialog.form
        );

        if (res.data.code === 200) {
          this.$message.success('通知发送成功');
          this.noticeDialog.visible = false;
        } else {
          this.$message.error(res.data.msg || '发送通知失败');
        }
      } catch (error) {
        console.error('发送通知失败:', error);
        this.$message.error('发送通知失败');
      }
    },
    // 获取课程列表
    async loadCourseList() {
      try {
        console.log(this.teacherInfo.id)
        const res = await this.$axios.get(this.$httpUrl + '/course/teacher/courses/' + this.teacherInfo.id);

        if (res.data.code === 200) {
          this.courseList = res.data.data;
        } else {
          this.$message.error('获取课程列表失败');
        }
      } catch (error) {
        console.error('加载课程列表失败:', error);
        this.$message.error('获取课程列表失败');
      }
    },

    // 打开课程编辑对话框
    openCourseDialog(course = null) {
      if (course) {
        this.courseDialog.title = '编辑课程';
        this.courseForm = {...course};
      } else {
        this.courseDialog.title = '添加课程';
        this.courseForm = {
          courseName: '',
          courseDescription: '',
          calendar: '',
          syllabus: '',
          teacherId: this.teacherInfo.id
        };
      }
      this.courseDialog.visible = true;
    },
    async viewResources(courseId) {
      try {
        const res = await this.$axios.get(this.$httpUrl + `/course/resources/${courseId}`);
        if (res.data.code === 200) {
          this.resourceListDialog.resourceList = res.data.data;
          this.resourceListDialog.visible = true;
        } else {
          this.$message.error('获取资源列表失败');
        }
      } catch (error) {
        console.error('获取资源列表失败:', error);
        this.$message.error('获取资源列表失败');
      }
    },

    async downloadResource(resourceId) {
      try {
        // 使用axios直接下载文件
        const response = await this.$axios.get(
            this.$httpUrl + '/course/resource/download/' + resourceId,
            {
              responseType: 'blob' // 重要：设置响应类型为blob
            }
        );

        // 从响应头中获取文件名
        const contentDisposition = response.headers['content-disposition'];
        let fileName = 'download'; // 默认文件名

        if (contentDisposition) {
          const fileNameMatch = contentDisposition.match(/filename="(.+)"/);
          if (fileNameMatch.length === 2) {
            fileName = decodeURIComponent(fileNameMatch[1]);
          }
        }

        // 创建Blob对象并下载
        const blob = new Blob([response.data], {
          type: response.headers['content-type']
        });

        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = fileName;
        link.click();
        window.URL.revokeObjectURL(link.href);

        this.$message.success('下载成功');
      } catch (error) {
        console.error('下载文件失败:', error);
        this.$message.error('下载失败，请重试');
      }
    },
    // 保存课程信息
    async saveCourse() {
      try {
        const valid = await this.$refs.courseForm.validate();
        if (!valid) return;

        const url = this.courseForm.courseId ? '/course/update' : '/course/save';
        const res = await this.$axios.post(this.$httpUrl + url, this.courseForm);

        if (res.data.code === 200) {
          this.$message.success('保存成功');
          this.courseDialog.visible = false;
          this.loadCourseList();
        } else {
          this.$message.error(res.data.msg || '保存失败');
        }
      } catch (error) {
        console.error('保存课程信息失败:', error);
        this.$message.error('保存失败');
      }
    },
    async deleteResource(resourceId) {
      try {
        await this.$confirm('确认删除该资源?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });

        const res = await this.$axios.delete(this.$httpUrl + `/course/resource/${resourceId}`);
        if(res.data.code === 200) {
          this.$message.success('删除成功');
          this.viewResources(this.resourceDialog.courseId);
        } else {
          this.$message.error('删除失败');
        }
      } catch(error) {
        console.error('删除资源失败:', error);
      }
    },

    // 查看选课学生
    async viewStudents(courseId) {
      try {
        const res = await this.$axios.get(this.$httpUrl + `/course/students/${courseId}`);
        if (res.data.code === 200) {
          this.studentDialog.studentList = res.data.data;
          this.studentDialog.courseId = courseId;
          this.studentDialog.visible = true;
        } else {
          this.$message.error('获取学生列表失败');
        }
      } catch (error) {
        console.error('获取学生列表失败:', error);
        this.$message.error('获取学生列表失败');
      }
    },

    // 上传课程资源
    openResourceDialog(courseId) {
      this.resourceDialog.courseId = courseId;
      this.resourceDialog.visible = true;
    },
    // Update beforeUpload method
    beforeUpload(file) {
      if (!this.resourceDialog.courseId) {
        this.$message.error('课程ID不能为空');
        return false;
      }

      const isValidType = this.uploadConfig.acceptTypes.includes(file.type);
      const isLessThanLimit = file.size / 1024 / 1024 < this.uploadConfig.maxSize;

      if (!isValidType) {
        this.$message.error('只能上传PDF、Word、PPT或Markdown格式的文件!');
        return false;
      }
      if (!isLessThanLimit) {
        this.$message.error(`文件大小不能超过 ${this.uploadConfig.maxSize}MB!`);
        return false;
      }
      return true;
    },

// Update success handler
    handleUploadSuccess(response, file) {
      if (response.code === 200) {
        this.$message.success('文件上传成功!');
        this.resourceDialog.fileList = []; // Clear file list after success
        this.resourceDialog.visible = false; // Close dialog
      } else {
        this.$message.error(response.msg || '文件上传失败!');
      }
    },


    // 处理分页大小变化
    handleSizeChange(val) {
      this.pageSize = val;
      this.loadCourseList();
    },

    // 处理当前页变化
    handleCurrentChange(val) {
      this.pageNum = val;
      this.loadCourseList();
    },

    // 获取当前教师信息
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
        console.log("当前用户",curUser.id)
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
          this.teacherInfo = res.data.data[0]

          // 更新sessionStorage中的用户信息
          curUser.name = this.teacherInfo.name
          curUser.phone = this.teacherInfo.phone
          sessionStorage.setItem("CurUser", JSON.stringify(curUser))
        } else {
          // 如果接口查询失败，直接使用sessionStorage中的信息
          this.teacherInfo = curUser
        }
      } catch (error) {
        console.error('获取用户信息失败：', error)
        // 如果接口调用失败，使用sessionStorage中的信息
        const curUser = this.getCurrentUser()
        if (curUser) {
          this.teacherInfo = curUser
        } else {
          this.$message.error('获取用户信息失败，请重新登录')
        }
      }
    },
  },

  created() {
    // 组件创建时，先尝试从sessionStorage加载用户信息
    const curUser = this.getCurrentUser()
    if (curUser) {
      this.teacherInfo = curUser
    }
    // 然后从后端获取最新信息
    this.loadUserInfo();
    this.loadCourseList();
  }
}
</script>

<template>
  <div class="course-management">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
          v-model="searchForm.courseName"
          placeholder="请输入课程名称"
          style="width: 200px"
          @keyup.enter.native="loadCourseList"
      />
      <el-button type="primary" @click="loadCourseList" style="margin-left: 10px">查询</el-button>
      <el-button type="primary" @click="openCourseDialog()" style="margin-left: 10px">添加课程</el-button>
    </div>

    <!-- 课程列表 -->
    <el-table
        :data="courseList"
        border
        style="width: 100%; margin-top: 20px"
    >
      <el-table-column prop="courseId" label="课程ID" width="120" />
      <el-table-column prop="courseName" label="课程名称" width="180" />
      <el-table-column prop="courseDescription" label="课程描述" />
      <!-- 在操作列添加查看资源按钮 -->
      <el-table-column label="操作" width="600" fixed="right">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="primary"
              @click="openCourseDialog(scope.row)"
          >编辑</el-button>
          <el-button
              size="mini"
              type="success"
              @click="viewStudents(scope.row.courseId)"
          >查看学生</el-button>
          <el-button
              size="mini"
              type="info"
              @click="viewResources(scope.row.courseId)"
          >查看资源</el-button>
          <el-button
              size="mini"
              type="warning"
              @click="openResourceDialog(scope.row.courseId)"
          >上传资源</el-button>
          <el-button
              size="mini"
              type="primary"
              @click="openNoticeDialog(scope.row.courseId)"
          >发送通知</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
        title="发送课程通知"
        :visible.sync="noticeDialog.visible"
        width="50%"
    >
      <el-form
          ref="noticeForm"
          :model="noticeDialog.form"
          :rules="noticeRules"
          label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="noticeDialog.form.title" placeholder="请输入通知标题"/>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
              type="textarea"
              v-model="noticeDialog.form.content"
              :rows="6"
              placeholder="请输入通知内容"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="noticeDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="sendNotice">发送</el-button>
      </div>
    </el-dialog>

      <!-- 添加资源列表对话框 -->


    <el-dialog title="课程资源列表" :visible.sync="resourceListDialog.visible" width="50%">
      <el-table :data="resourceListDialog.resourceList" border>
        <el-table-column prop="fileName" label="文件名称"/>

        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="downloadResource(scope.row.resourceId)">下载</el-button>
            <el-button size="mini" type="danger" @click="deleteResource(scope.row.resourceId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 分页器 -->
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top: 20px; text-align: right"
    />

    <!-- 课程信息对话框 -->
    <el-dialog
        :title="courseDialog.title"
        :visible.sync="courseDialog.visible"
        width="50%"
    >
      <el-form
          ref="courseForm"
          :model="courseForm"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="courseForm.courseName" />
        </el-form-item>
        <el-form-item label="课程描述" prop="courseDescription">
          <el-input
              type="textarea"
              v-model="courseForm.courseDescription"
              :rows="4"
          />
        </el-form-item>
        <el-form-item label="教学日历" prop="calendar">
          <el-input
              type="textarea"
              v-model="courseForm.calendar"
              :rows="4"
          />
        </el-form-item>
        <el-form-item label="教学大纲" prop="syllabus">
          <el-input
              type="textarea"
              v-model="courseForm.syllabus"
              :rows="4"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="courseDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveCourse">确定</el-button>
      </div>
    </el-dialog>

    <!-- 学生列表对话框 -->
    <el-dialog
        title="选课学生列表"
        :visible.sync="studentDialog.visible"
        width="50%"
    >
      <el-table :data="studentDialog.studentList" border>
        <el-table-column prop="id" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="phone" label="联系电话" />
      </el-table>
    </el-dialog>

    <!-- 资源上传对话框 -->
    <el-dialog
        title="上传课程资源"
        :visible.sync="resourceDialog.visible"
        width="50%"
    >
      <el-upload
          :action="$httpUrl + '/course/resource/upload'"
          :data="{
      courseId: resourceDialog.courseId,
      fileType: 'document' // Add default fileType
    }"
          :before-upload="beforeUpload"
          :on-success="handleUploadSuccess"
          :headers="uploadHeaders"
          multiple
          :file-list="resourceDialog.fileList"
          :accept="uploadConfig.acceptExts"
      >
      <el-button size="small" type="primary">点击上传</el-button>
        <div slot="tip" class="el-upload__tip">
          支持上传PDF、Word、PPT、Markdown格式文件，单个文件不超过100MB
        </div>
      </el-upload>
    </el-dialog>
  </div>
</template>

<style scoped>
.course-management {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}
</style>