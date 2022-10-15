function checkSignin() {
    var email = document.getElementById("signin_email");
    var password = document.getElementById("signin_password");
    var email_test = /^([a-z0-9]|\.)+@([a-z0-9]|\.)+$/;

    if ("" == email.value) {
        alert("邮箱为空");
        email.focus();
        return false;
    } else if (email_test.test(email.value) == false) {
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
    var email = document.getElementById("signup_email");
    var email_test = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;

    if ("" == email.value) {
        alert("邮箱为空");
        email.focus();
        return false;
    } else if (email_test.test(email.value) == false) {
        alert("邮箱格式错误");
        email.focus();
        return false;
    }
    sessionStorage.removeItem("resultDTO");
    return true;
}


function checkPassword() {
    var password = document.getElementById("password");
    var pwdRegex = /^(?=.*[0-9])(?=.*[a-zA-Z]).{8,30}$/;
    if (pwdRegex.test(password.value) == false) {
        alert("您的密码复杂度太低（密码中必须包含字母、数字），且字数在8-30字");
        password.focus();
        return false;
    }
    return true;
}

// 暂时没用
function deleteNotification(e) {
    if (confirm("确认删除该提醒？")) {
        var id = e.getAttribute("data");
        $.ajax({
            type: "POST",
            url: "/notification/delete",
            contentType: 'application/json',
            data: id,
            success: function (response) {
                if (response.code == 200) {
                    document.getElementById("notification-" + id).remove();
                } else {
                    if (response.code == 2003) {
                        var isAccepted = confirm(response.message);
                        if (isAccepted) {
                            window.open("/login");
                        }
                    } else {
                        alert(response.message);
                    }
                }
            },
            dataType: "json"
        });
    } else {
    }
}

// GitHub 登录
function checkGitHub() {
    sessionStorage.removeItem("resultDTO");
}

function deleteDraft(e) {
    const id = e.getAttribute("data");
    if (confirm("确认删除此草稿？")) {
        $.ajax({
            type: "POST",
            url: "/draft/delete",
            contentType: 'application/json',
            data: id,
            success: function (response) {
                if (response.code == 200) {
                    location.reload();
                } else {
                    if (response.code == 2003) {
                        var isAccepted = confirm(response.message);
                        if (isAccepted) {
                            window.open("/login");
                        }
                    } else {
                        alert(response.message);
                    }
                }
            },
            dataType: "json"
        });
    } else {
    }
}

// 提交问题回复
function postQuestionComment() {
    var questionId = $("#question-id").val();
    var content = $("#comment-content").val();
    comment2target(questionId, 1, content);
}

// 提交问题评论回复
function commentQuestionCommemnt(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();
    comment2target(commentId, 2, content);
}

// 提交文章回复
function postArticleComment() {
    var articleId = $("#article-id").val();
    var content = $("#comment-content").val();
    comment2target(articleId, 3, content);
}

// 提交文章评论回复
function commentArticleComment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();
    comment2target(commentId, 4, content);
}

function comment2target(targetId, type, content) {
    if (!content) {
        alert("评论内容不能为空");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: 'application/json',
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                location.reload();
            } else {
                if (response.code == 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("/login");
                    }
                } else {
                    alert(response.message);
                }
            }
        },
        dataType: "json"
    });
}

// 展开二级评论
function collapseComments(e) {

    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);

    //展开状态
    if (comments.hasClass("in")) {
        comments.removeClass("in");
        e.classList.remove("active");
    }
    //收起状态
    else {
        var subCommentContainer = $("#comment-" + id);
        if (subCommentContainer.children().length == 1) {
            $.getJSON("/comment/" + id, function (data) {
                var items = [];
                $.each(data.data.reverse(), function (index, comment) {
                    var manager = document.getElementById("manager").value;
                    var html = "<div class=\"col-lg-12 col-md-12 col-sm-12 col-xs-12 comments\">\n" +
                        "                                    <div>\n" +
                        "                                        <div class=\"media-left\">\n" +
                        "                                            <img class=\"media-object img-rounded\"\n" +
                        "                                                 src=\"https://avatars.githubusercontent.com/u/52725517?v=4\">\n" +
                        "                                        </div>\n" +
                        "                                        <div class=\"media-body\"><h5 class=\"media-heading\">" + comment.user.name + (manager == "true" ? "<span class='text-desc'> Cretor-ID: " + comment.commentator + "</span>" : "") + "</h5>\n" +
                        "                                            <div class=\"comment-content\">" + comment.content + "</div>\n" +
                        "                                            <div class=\"menu\"><span\n" +
                        "                                                    class=\"glyphicon glyphicon-thumbs-up icon\" onclick='like(this)'></span><span\n" +
                        "                                                    class=\"pull-right\">" + moment(comment.gmtCreate).format('YYYY-MM-DD HH:mm') + "</span></div>\n" +
                        "                                        </div>\n" +
                        "                                    </div>\n" +
                        "                                </div>";
                    var commentElement = html;
                    subCommentContainer.prepend(commentElement);
                });
                comments.addClass("in");
                e.classList.add("active");
            });
        } else {
            comments.addClass("in");
            e.classList.add("active");
        }
    }
}

// TODO: 点赞功能需要 redis 辅助，还在学
function like(e) {
    alert("点赞功能还在学捏 QAQ");
    // var id = e.getAttribute("data-id");
    // var like = document.getElementById("comment-" + id).getElementsByClassName("like-count")[0];
    // if (like) {
    //     like.innerHTML = parseInt(like.innerHTML) + 1;
    // }
    // console.log(comment);
    // $.ajax({
    //     type: "POST",
    //     url: "/comment/like",
    //     contentType: 'application/json',
    //     data: id,
    //     success: function (response) {
    //         if (response.code == 200) {
    //             document.getElementById("comment-" + id);
    //         } else {
    //             if (response.code == 2003) {
    //                 var isAccepted = confirm(response.message);
    //                 if (isAccepted) {
    //                     window.open("/login");
    //                 }
    //             } else {
    //                 alert(response.message);
    //             }
    //         }
    //     },
    //     dataType: "json"
    // });

}

Array.prototype.remove = function (from, to) {
    var rest = this.slice((to || from) + 1 || this.length);
    this.length = from < 0 ? this.length + from : from;
    return this.push.apply(this, rest);
};

// 插入tag
function selectTag(e) {
    var value = e.getAttribute("data-tag");
    var previous = $("#tag").val();
    var clazz = e.getAttribute("class");
    if (clazz.indexOf("tag-default") > -1) {
        if (previous) {
            $("#tag").val(previous + ',' + value);
        } else {
            $("#tag").val(value);
        }
        e.classList.remove('tag-default');
        e.classList.add('tag-active');
    } else if (clazz.indexOf("tag-active") > -1) {
        if (value.indexOf("+") > -1) {
            value = value.replaceAll("+", "\\" + "\\" + "+");
        }
        var pattern = new RegExp("," + value + "|" + value);
        var split = previous.split(",");
        split.remove(split.indexOf(value));
        previous = split.join(",");
        $("#tag").val(previous);
        e.classList.remove('tag-active');
        e.classList.add('tag-default');
    }
}

// community-publish.html 异常上传处理
function publishCheck() {
    var title = document.getElementById("title");
    var description = document.getElementById("description");
    var tag = document.getElementById("tag");

    if ("" == title.value) {
        alert("标题不能为空");
        return false;
    }
    if ("" == description.value) {
        alert("内容不能为空");
        return false;
    }
    if ("" == tag.value) {
        alert("标签不能为空");
        return false;
    }
    sessionStorage.removeItem("resultDTO");
    return true;
}

function articleSave() {
    var id = document.getElementById("draftId").value;
    var title = document.getElementById("title").value;
    var description = document.getElementById("description").value;
    var tag = document.getElementById("tag").value;
    var type = 1;

    if ("" == title && "" == description && "" == tag) {
        alert("标题 内容 标签 不能都为空");
        return false;
    }
    sessionStorage.removeItem("resultDTO");

    saveAsDraft(id, title, description, tag, type);
}

function questionSave() {
    var id = document.getElementById("draftId").value;
    var title = document.getElementById("title").value;
    var description = document.getElementById("description").value;
    var tag = document.getElementById("tag").value;
    var type = 0;

    if ("" == title && "" == description && "" == tag) {
        alert("标题 内容 标签 不能都为空");
        return false;
    }
    sessionStorage.removeItem("resultDTO");

    saveAsDraft(id, title, description, tag, type);
}

// community-publish.html 保存草稿处理
function saveAsDraft(id, title, description, tag, type) {
    $.ajax({
        type: "POST",
        url: "/draft",
        contentType: 'application/json',
        data: JSON.stringify({
            "id": id,
            "title": title,
            "description": description,
            "tag": tag,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                // response.data 保存的是草稿的 id，通过 js 访问
                alert("已保存为草稿，可在 '我的草稿' 中查看并编辑！")
                location.href = "/draft/" + response.data;
            } else {
                if (response.code == 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("/login");
                    }
                } else {
                    alert(response.message);
                }
            }
        },
        dataType: "json"
    });
}

// 修改工具
function modifyTool(e) {
    var id = e.getAttribute("data-id");
    var item =
        location.href = "/manage/" + id;
}

// 删除工具
function deleteTool(e) {
    var id = e.getAttribute("data-id");
    if (confirm("确认删除？"))
        $.ajax({
            type: "POST",
            url: "/depot/delete",
            contentType: 'application/json',
            data: id,
            success: function (response) {
                if (response.code == 200) {
                    document.getElementById('tool-' + id).remove();
                } else {
                    if (response.code == 2003) {
                        var isAccepted = confirm(response.message);
                        if (isAccepted) {
                            window.open("/login");
                        }
                    } else {
                        alert(response.message);
                    }
                }
            }
        })
    else
        return;
}

// 添加工具
function addTool() {
    location.href = "/depot/publish";
}

// 选择管理的对象
function manageItem(type, e) {
    $("table").children().remove();
    let item;
    switch (type) {
        case 0: {
            item = "article";
            break;
        }
        case 1: {
            item = "question";
            break;
        }
        case 2: {
            item = "tool";
            break;
        }
        default: {
            item = null;
        }
    }
    document.getElementById("table").setAttribute("data", item);
    document.getElementsByName("item-btn").forEach(b => b.classList.remove('active'));
    e.className += ' active';

    var page = e.getAttribute("page");

    $.ajax({
        url: "/manage/" + item + "?page=" + page,
        success: function (response, status) {
            if (response.code == 200) {
                var data = response.data;

                var ids = new Array();
                var titles = new Array();
                var tags = new Array();
                // TODO: 搜索栏
                if (data && Object.keys(data[0]).includes("title")) {
                    // 文章、问题、工具
                    var title = "<thead>\n" +
                        "                    <tr>\n" +
                        "                        <th>编号</th>\n" +
                        "                        <th>id</th>\n" +
                        "                        <th>title</th>\n" +
                        "                        <th>tag</th>\n" +
                        "                        <th>Creator ID</th>\n" +
                        "                        <th>操作</th>\n" +
                        "                    </tr>\n" +
                        "                    </thead>";
                    var content = "";
                    for (let i = 0; i < data.length; i++) {
                        content += "<thead>\n" +
                            "                    <tr id=\"item-" + (page * 10 + i) + "\">\n" +
                            "                        <th style='min-width: 50px;'>" + (page * 10 + i) + "</th>\n" +
                            "                        <th>" + data[i].id + "</th>\n" +
                            "                        <th>\"" + data[i].title + "\"</th>\n" +
                            "                        <th>\"" + data[i].tag + "\"</th>\n" +
                            "                        <th style='min-width: 90px;'>\"" + data[i].creator + "\"</th>\n" +
                            "                        <th style='min-width: 125px;'>\n" +
                            "                            <div class=\"btn-group\" role=\"group\" aria-label=\"...\">\n" +
                            "                                <button type=\"button\" class=\"btn btn-default\" th:data-id=\"${tool.getId()}\"\n" +
                            "                                        onclick=\"modifyTool(this)\">修改\n" +
                            "                                </button>\n" +
                            "                                <button type=\"button\" class=\"btn btn-danger\" th:data-id=\"${tool.getId()}\"\n" +
                            "                                        onclick=\"deleteTool(this)\">删除\n" +
                            "                                </button>\n" +
                            "                            </div>\n" +
                            "                        </th>\n" +
                            "                    </tr>\n" +
                            "                    </thead>"
                    }
                    $("table").append(title);
                    $("table").append(content);

                } else if (data && Object.keys(data[0]).includes("name")) {
                    // 用户
                    var title = "<thead>\n" +
                        "                    <tr>\n" +
                        "                        <th>编号</th>\n" +
                        "                        <th>id</th>\n" +
                        "                        <th>name</th>\n" +
                        "                        <th>email</th>\n" +
                        "                        <th>操作</th>\n" +
                        "                    </tr>\n" +
                        "                    </thead>";
                    var content = "";
                    for (let i = 0; i < data.length; i++) {
                        content += "<thead>\n" +
                            "                    <tr id=\"item-" + (page * 10 + i) + "\">\n" +
                            "                        <th>" + (page * 10 + i) + "</th>\n" +
                            "                        <th>" + data[i].id + "</th>\n" +
                            "                        <th>\"" + data[i].name + "\"</th>\n" +
                            "                        <th>\"" + data[i].email + "\"</th>\n" +
                            "                        <th>\n" +
                            "                            <div class=\"btn-group\" role=\"group\" aria-label=\"...\">\n" +
                            "                                <button type=\"button\" class=\"btn btn-default\" th:data-id=\"${tool.getId()}\"\n" +
                            "                                        onclick=\"modifyTool(this)\">修改\n" +
                            "                                </button>\n" +
                            "                                <button type=\"button\" class=\"btn btn-danger\" th:data-id=\"${tool.getId()}\"\n" +
                            "                                        onclick=\"deleteTool(this)\">删除\n" +
                            "                                </button>\n" +
                            "                            </div>\n" +
                            "                        </th>\n" +
                            "                    </tr>\n" +
                            "                    </thead>"
                    }
                    $("table").append(title);
                    $("table").append(content);


                }

            } else {
                if (response.code == 2003) {
                    if (confirm(response.message)) {
                        window.open("/login");
                    }
                } else if (response.code == 2014) {
                    alert(response.message);
                    location.href = "/";
                } else {
                    alert(response.message);
                }
            }
        }
    });
}