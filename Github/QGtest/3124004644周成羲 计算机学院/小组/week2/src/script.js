new Vue({
    el: '#app',
    data: {
        isLogin: true,
        account: '', // 登录时的账号（手机号/邮箱）
        phone: '', // 注册时的手机号
        email: '', // 注册时的邮箱
        password: '',
        confirmPassword: '',
        showError: false // 是否显示错误提示
    },
    methods: {
        login() {
            this.showError = false; // 重置错误提示
            if (!this.account || !this.password) {
                this.showError = true; // 显示错误提示
                return;
            }
            // 验证账号格式（手机号或邮箱）
            if (!this.validateAccount(this.account)) {
                alert('账号必须是有效的手机号或邮箱');
                return;
            }
            // 模拟密码验证（这里假设正确密码为 '123456'）
            if (this.password !== '123456') {
                alert('密码错误');
                return;
            }
            alert('登录成功');
        },
        register() {
            this.showError = false; // 重置错误提示
            if (!this.phone || !this.email || !this.password || !this.confirmPassword) {
                this.showError = true; // 显示错误提示
                return;
            }
            if (this.password !== this.confirmPassword) {
                alert('两次输入的密码不一致');
                return;
            }
            // 验证手机号和邮箱格式
            if (!this.validatePhone(this.phone) || !this.validateEmail(this.email)) {
                alert('手机号或邮箱格式不正确');
                return;
            }
            alert('注册成功');
        },
        validateAccount(account) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            const phoneRegex = /^1[3-9]\d{9}$/; // 简单的中国大陆手机号正则
            return emailRegex.test(account) || phoneRegex.test(account);
        },
        validatePhone(phone) {
            const phoneRegex = /^1[3-9]\d{9}$/; // 简单的中国大陆手机号正则
            return phoneRegex.test(phone);
        },
        validateEmail(email) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            return emailRegex.test(email);
        }
    }
});