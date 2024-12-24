<script >
export default {
  name: "Header",
  data(){
    return {
      user: null, // 初始值为 null
    }
  },
  props:{
    icon:String
  },
  methods:{
    toUser(){
      console.log('to_user')
      this.$router.push("/Home")
    },
    logout(){
      console.log('logout')

      this.$confirm('您确定要退出登录吗?', '提示', {
        confirmButtonText: '确定',  //确认按钮的文字显示
        type: 'warning',
        center: true, //文字居中显示

      })
          .then(() => {
            this.$message({
              type:'success',
              message:'退出登录成功'
            })

            this.$router.push("/")
            sessionStorage.clear()
            this.user = null; // 清空用户数据
          })
          .catch(() => {
            this.$message({
              type:'info',
              message:'已取消退出登录'
            })
          })

    },
    collapse(){
      this.$emit('doCollapse')
    }

  },
  created() {
    try {
      const userData = sessionStorage.getItem("CurUser");
      if (userData) {
        const parsedData = JSON.parse(userData);
        this.user = parsedData.data; // 只获取data部分
        console.log("用户数据加载成功：", this.user);

        // 确保成功获取到用户数据后再跳转
        if (this.user && this.user.id) {
          this.$router.push("/Home");
        } else {
          console.warn("用户数据不完整");
          this.$router.push("/login");
        }
      } else {
        console.warn("未找到用户数据");
        this.$router.push("/login");
      }
    } catch (e) {
      console.error("加载用户数据失败：", e);
      this.$router.push("/login");
    }
  }

}

</script>

<template>
  <div style="display: flex;line-height: 60px;">
    <div style="margin-top: 8px;">
      <i :class="icon" style="font-size: 20px;cursor: pointer;" @click="collapse"></i>
    </div>
    <div style="flex: 1;text-align: center;font-size: 34px;">
      <span>欢迎来到智慧课程平台</span>
    </div>
    <el-dropdown>
      <span>{{user.name}}</span><i class="el-icon-arrow-down" style="margin-left: 5px;"></i>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item @click.native="toUser">个人中心</el-dropdown-item>
        <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>

  </div>
</template>

<style>



</style>