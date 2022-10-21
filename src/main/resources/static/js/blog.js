Array.prototype.remove = function (from, to) {
    var rest = this.slice((to || from) + 1 || this.length);
    this.length = from < 0 ? this.length + from : from;
    return this.push.apply(this, rest);
};

String.prototype.format = function () {
    var formatted = this;
    for (var arg in arguments) {
        formatted = formatted.replace("{" + arg + "}", arguments[arg]);
    }
    return formatted;
};

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

// 选择管理的对象
$(document).ready(function () {
    $("#manage-article").on("click", function () {
        manageItem(0, 1, this);
    });
    $("#manage-question").on("click", function () {
        manageItem(1, 1, this);
    });
    $("#manage-tool").on("click", function () {
        manageItem(2, 1, this);
    });
})

// 选择管理的对象
function manageItem(type, page, e) {
    $("tbody").children().remove();
    var pageNav = document.getElementById("page-nav");
    if (pageNav)
        pageNav.remove();


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

    $.ajax({
        url: "/manage/" + item + "?page=" + page,
        success: function (response, status) {
            if (response.code == 200) {
                var paginationDTO = response.data;
                var data = paginationDTO.data;

                page = paginationDTO.page;

                // TODO: 搜索栏
                // 文章、问题、工具
                var content = "";
                for (let i = 0; i < data.length; i++) {
                    content +=
                        "                    <tr id=\"item-" + ((page - 1) * 10 + i + 1) + "\">\n" +
                        "                        <td style='min-width: 50px;'>" + ((page - 1) * 10 + i + 1) + "</td>\n" +
                        "                        <td>" + data[i].id + "</td>\n" +
                        "                        <td>" + data[i].title + "</td>\n" +
                        "                        <td>" + data[i].tag + "</td>\n" +
                        "                        <td>" + data[i].user.name + "</td>\n" +
                        "                        <td>" + data[i].creator + "</td>\n" +
                        "                        <td>\n" +
                        "                            <div class=\"btn-group\" role=\"group\" aria-label=\"...\">\n" +
                        "                                <button type=\"button\" class=\"btn btn-default\" data='" + (data[i].id) + "'\n" +
                        "                                        onclick=\"manageModify(this)\">修改\n" +
                        "                                </button>\n" +
                        "                                <button type=\"button\" class=\"btn btn-danger\" data='" + (data[i].id) + "' \n" +
                        "                                        onclick=\"manageDelete(this)\">删除\n" +
                        "                                </button>\n" +
                        "                            </div>\n" +
                        "                        </td>\n" +
                        "                    </tr>\n";
                }
                $("tbody").append(content);

                var pages = "";
                for (let i = 0; i < paginationDTO.pages.length; i++) {
                    pages +=
                        "<li class='" + (paginationDTO.pages[i] === page ? ' active' : '') + "'>\n" +
                        "    <a href='#' onclick='manageItem(" + "{0}, {1}, this".format(type, paginationDTO.pages[i]) + ")'>" + paginationDTO.pages[i] + "</a>\n" +
                        "</li>\n";
                }

                var pagi =
                    "<nav aria-label='Page navigation' id='page-nav'" + (paginationDTO.showPageNav === true ? '' : ' hidden') + ">\n" +
                    "    <ul class='pagination'>\n" +
                    "        <li class='" + (paginationDTO.showFirstPage === true ? '' : ' disabled') + "'>\n" +
                    "            <a href='#' aria-label='First' onclick='manageItem(" + ("{0}, {1}, this".format(type, 1)) + ")'>\n" +
                    "                <span aria-hidden='true'>&laquo;</span>\n" +
                    "            </a>\n" +
                    "        </li>\n" +
                    "        <li class='" + (paginationDTO.showPrevious === true ? '' : ' disabled') + "'>\n" +
                    "            <a href='#' aria-label='Previous' onclick='manageItem(" + ("{0}, {1}, this".format(type, page - 1)) + ")'>\n" +
                    "                <span aria-hidden='true'>&lt;</span>\n" +
                    "            </a>\n" +
                    "        </li>\n" +
                    pages +
                    "        <li class='" + (paginationDTO.showNext === true ? '' : ' disabled') + "'>\n" +
                    "            <a href='#' aria-label='Next' onclick='manageItem(" + ("{0}, {1}, this".format(type, page + 1)) + ")'>\n" +
                    "                <span aria-hidden='true'>&gt;</span>\n" +
                    "            </a>\n" +
                    "        </li>\n" +
                    "        <li class='" + (paginationDTO.showEndPage === true ? '' : ' disabled') + "'>\n" +
                    "            <a href='#' aria-label='End' onclick='manageItem(" + ("{0}, {1}, this".format(type, paginationDTO.totalPage)) + ")'>\n" +
                    "                <span aria-hidden='true'>&raquo;</span>\n" +
                    "            </a>\n" +
                    "        </li>\n" +
                    "    </ul>\n" +
                    "</nav>";


                $("table").parent().append(pagi);
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

function manageModify(e) {
    var id = e.getAttribute("data");
    var section = document.getElementById("table").getAttribute("data");
    location.href = "/manage/" + section + "/modify?id=" + id;
}

function manageDelete(e) {
    var id = e.getAttribute("data");
    var section = document.getElementById("table").getAttribute("data");
    if (confirm("确认要删除此" + (section === "article" ? "文章" : (section === "question" ? "问题":(section === "tool" ? "工具":""))) + "?")) {
        $.ajax({
            type: "POST",
            url: "/manage/" + section + "/delete?id=" + id,
            contentType: 'application/json',
            data: id,
            success: function (response) {
                if (response.code == 200) {
                    // response.data 保存的是草稿的 id，通过 js 访问
                    alert("已删除！")
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
        })
    }
}