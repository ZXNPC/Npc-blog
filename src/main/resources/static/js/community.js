// 提交回复
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId, 1, content);
}

// 提交二级回复
function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();
    comment2target(commentId, 2, content);
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

                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    }))
                        .append($("<div/>", {
                            "class": "comment-content",
                            "html": comment.content
                        }))
                        .append($("<div/>", {
                            "class": "menu"
                        }).append($("<span/>", {
                            "class": "glyphicon glyphicon-thumbs-up icon"
                        })).append($("<span/>", {
                            "class": "pull-right",
                            "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                        })));

                    var mediaElement = $("<div/>", {
                    }).append(mediaLeftElement)
                        .append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments",
                    }).append(mediaElement);
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

// 展示selectTag
function showSelectTag() {
    $("#select-tag").show();
}

// 插入tag
function selectTag(e) {
    var value = e.getAttribute("data-tag");
    var previous = $("#tag").val();
    if (!previous.split(',').includes(value)) {
        if (previous) {
            $("#tag").val(previous + ',' + value);
        } else {
            $("#tag").val(value);
        }
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
                location.href="/draft/" + response.data;
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