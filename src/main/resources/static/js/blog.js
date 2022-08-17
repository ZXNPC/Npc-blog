function checkSignin() {
    var email = document.getElementById("signin_email");
    var password = document.getElementById("signin_password");
    var email_test = /^([a-z0-9]|\.)+@([a-z0-9]|\.)+$/;

    if ("" == email.value) {
        alert("邮箱为空");
        email.focus();
        return false;
    }
    else if (email_test.test(email.value) == false) {
        alert("邮箱格式错误")
        email.focus();
        return false;
    }
    if ("" == password.value) {
        alert("密码为空");
        password.focus();
        return false;
    }
    sessionStorage.removeItem("resultDTO");
    return true;
}

function checkSignup() {
    var user = document.getElementById("signup_user");
    var email = document.getElementById("signup_email");
    var email_test = /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/;

    if ("" == user) {
        alert("用户名为空");
        user.focus();
        return false;
    }
    if ("" == email) {
        alert("邮箱为空");
        email.focus();
        return false;
    }
    else if (email_test.test(email)) {
        alert("邮箱格式错误");
        email.focus();
        return false;
    }
    sessionStorage.removeItem("resultDTO");
    return true;
}


function checkPassword() {
    var password = document.getElementById("password");
    var password_repeat = document.getElementById("password_repeat");
    var pwdRegex = /^(?=.*[0-9])(?=.*[a-zA-Z]).{8,30}$/;
    if (!pwdRegex.test(password)) {
        alert("您的密码复杂度太低（密码中必须包含字母、数字）");
        password.focus();
        return false;
    }
    if(password.value != password_repeat.value) {
        alert("两次输入的密码不匹配，请重新输入！")
        password_repeat.focus();
        return false;
    }
}
