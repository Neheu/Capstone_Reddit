package com.udacity.project.reddit.capstone.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neha on 04-09-2017.
 */

public class GetCommentsModel {
    private List<RootData> comments;

    public GetCommentsModel() {
        comments = new ArrayList<>();
    }

    public class RootData {
        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data data;

    }


    public class Child {

        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data_ data;

    }


    public class Child_ {

        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data___ data;

    }


    public class Child__ {

        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data_____ data;

    }


    public class Child___ {

        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data_______ data;

    }


    public class Child____ {

        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data_________ data;

    }


    public class Data {

        @SerializedName("modhash")
        @Expose
        public String modhash;
        @SerializedName("children")
        @Expose
        public List<Child> children = null;
        @SerializedName("after")
        @Expose
        public Object after;
        @SerializedName("before")
        @Expose
        public Object before;

    }


    public class Data_ {

        @SerializedName("domain")
        @Expose
        public String domain;
        @SerializedName("approved_at_utc")
        @Expose
        public Object approvedAtUtc;
        @SerializedName("banned_by")
        @Expose
        public Object bannedBy;
        @SerializedName("media_embed")
        @Expose
        public MediaEmbed mediaEmbed;
        @SerializedName("thumbnail_width")
        @Expose
        public Object thumbnailWidth;
        @SerializedName("subreddit")
        @Expose
        public String subreddit;
        @SerializedName("selftext_html")
        @Expose
        public String selftextHtml;
        @SerializedName("likes")
        @Expose
        public Object likes;
        @SerializedName("suggested_sort")
        @Expose
        public Object suggestedSort;
        @SerializedName("user_reports")
        @Expose
        public List<Object> userReports = null;
        @SerializedName("secure_media")
        @Expose
        public Object secureMedia;
        @SerializedName("link_flair_text")
        @Expose
        public String linkFlairText;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("banned_at_utc")
        @Expose
        public Object bannedAtUtc;
        @SerializedName("view_count")
        @Expose
        public Object viewCount;
        @SerializedName("archived")
        @Expose
        public Boolean archived;
        @SerializedName("clicked")
        @Expose
        public Boolean clicked;
        @SerializedName("report_reasons")
        @Expose
        public Object reportReasons;
        @SerializedName("title")
        @Expose
        public String title;
        @SerializedName("num_crossposts")
        @Expose
        public Integer numCrossposts;
        @SerializedName("saved")
        @Expose
        public Boolean saved;
        @SerializedName("can_mod_post")
        @Expose
        public Boolean canModPost;
        @SerializedName("is_crosspostable")
        @Expose
        public Boolean isCrosspostable;
        @SerializedName("score")
        @Expose
        public Integer score;
        @SerializedName("approved_by")
        @Expose
        public Object approvedBy;
        @SerializedName("over_18")
        @Expose
        public Boolean over18;
        @SerializedName("hidden")
        @Expose
        public Boolean hidden;
        @SerializedName("num_comments")
        @Expose
        public Integer numComments;
        @SerializedName("thumbnail")
        @Expose
        public String thumbnail;
        @SerializedName("subreddit_id")
        @Expose
        public String subredditId;
        @SerializedName("hide_score")
        @Expose
        public Boolean hideScore;
        @SerializedName("edited")
        @Expose
        public Boolean edited;
        @SerializedName("link_flair_css_class")
        @Expose
        public String linkFlairCssClass;
        @SerializedName("author_flair_css_class")
        @Expose
        public String authorFlairCssClass;
        @SerializedName("contest_mode")
        @Expose
        public Boolean contestMode;
        @SerializedName("gilded")
        @Expose
        public Integer gilded;
        @SerializedName("locked")
        @Expose
        public Boolean locked;
        @SerializedName("downs")
        @Expose
        public Integer downs;
        @SerializedName("brand_safe")
        @Expose
        public Boolean brandSafe;
        @SerializedName("secure_media_embed")
        @Expose
        public SecureMediaEmbed secureMediaEmbed;
        @SerializedName("removal_reason")
        @Expose
        public Object removalReason;
        @SerializedName("can_gild")
        @Expose
        public Boolean canGild;
        @SerializedName("thumbnail_height")
        @Expose
        public Object thumbnailHeight;
        @SerializedName("parent_whitelist_status")
        @Expose
        public String parentWhitelistStatus;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("spoiler")
        @Expose
        public Boolean spoiler;
        @SerializedName("permalink")
        @Expose
        public String permalink;
        @SerializedName("num_reports")
        @Expose
        public Object numReports;
        @SerializedName("whitelist_status")
        @Expose
        public String whitelistStatus;
        @SerializedName("stickied")
        @Expose
        public Boolean stickied;
        @SerializedName("created")
        @Expose
        public Integer created;
        @SerializedName("url")
        @Expose
        public String url;
        @SerializedName("author_flair_text")
        @Expose
        public String authorFlairText;
        @SerializedName("quarantine")
        @Expose
        public Boolean quarantine;
        @SerializedName("author")
        @Expose
        public String author;
        @SerializedName("created_utc")
        @Expose
        public Integer createdUtc;
        @SerializedName("subreddit_name_prefixed")
        @Expose
        public String subredditNamePrefixed;
        @SerializedName("distinguished")
        @Expose
        public Object distinguished;
        @SerializedName("media")
        @Expose
        public Object media;
        @SerializedName("upvote_ratio")
        @Expose
        public Double upvoteRatio;
        @SerializedName("mod_reports")
        @Expose
        public List<Object> modReports = null;
        @SerializedName("is_self")
        @Expose
        public Boolean isSelf;
        @SerializedName("visited")
        @Expose
        public Boolean visited;
        @SerializedName("subreddit_type")
        @Expose
        public String subredditType;
        @SerializedName("is_video")
        @Expose
        public Boolean isVideo;
        @SerializedName("ups")
        @Expose
        public Integer ups;
        @SerializedName("link_id")
        @Expose
        public String linkId;
        @SerializedName("replies")
        @Expose
        public Replies replies;
        @SerializedName("parent_id")
        @Expose
        public String parentId;
        @SerializedName("body")
        @Expose
        public String body;
        @SerializedName("collapsed")
        @Expose
        public Boolean collapsed;
        @SerializedName("is_submitter")
        @Expose
        public Boolean isSubmitter;
        @SerializedName("collapsed_reason")
        @Expose
        public Object collapsedReason;
        @SerializedName("body_html")
        @Expose
        public String bodyHtml;
        @SerializedName("score_hidden")
        @Expose
        public Boolean scoreHidden;
        @SerializedName("controversiality")
        @Expose
        public Integer controversiality;
        @SerializedName("depth")
        @Expose
        public Integer depth;

    }


    public class Data__ {

        @SerializedName("modhash")
        @Expose
        public String modhash;
        @SerializedName("children")
        @Expose
        public List<Child_> children = null;
        @SerializedName("after")
        @Expose
        public Object after;
        @SerializedName("before")
        @Expose
        public Object before;

    }


    public class Data___ {

        @SerializedName("subreddit_id")
        @Expose
        public String subredditId;
        @SerializedName("approved_at_utc")
        @Expose
        public Object approvedAtUtc;
        @SerializedName("banned_by")
        @Expose
        public Object bannedBy;
        @SerializedName("removal_reason")
        @Expose
        public Object removalReason;
        @SerializedName("link_id")
        @Expose
        public String linkId;
        @SerializedName("likes")
        @Expose
        public Object likes;
        @SerializedName("replies")
        @Expose
        public Replies_ replies;
        @SerializedName("user_reports")
        @Expose
        public List<Object> userReports = null;
        @SerializedName("saved")
        @Expose
        public Boolean saved;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("banned_at_utc")
        @Expose
        public Object bannedAtUtc;
        @SerializedName("gilded")
        @Expose
        public Integer gilded;
        @SerializedName("archived")
        @Expose
        public Boolean archived;
        @SerializedName("report_reasons")
        @Expose
        public Object reportReasons;
        @SerializedName("author")
        @Expose
        public String author;
        @SerializedName("can_mod_post")
        @Expose
        public Boolean canModPost;
        @SerializedName("ups")
        @Expose
        public Integer ups;
        @SerializedName("parent_id")
        @Expose
        public String parentId;
        @SerializedName("score")
        @Expose
        public Integer score;
        @SerializedName("approved_by")
        @Expose
        public Object approvedBy;
        @SerializedName("downs")
        @Expose
        public Integer downs;
        @SerializedName("body")
        @Expose
        public String body;
        @SerializedName("edited")
        @Expose
        public Boolean edited;
        @SerializedName("author_flair_css_class")
        @Expose
        public String authorFlairCssClass;
        @SerializedName("collapsed")
        @Expose
        public Boolean collapsed;
        @SerializedName("is_submitter")
        @Expose
        public Boolean isSubmitter;
        @SerializedName("collapsed_reason")
        @Expose
        public Object collapsedReason;
        @SerializedName("body_html")
        @Expose
        public String bodyHtml;
        @SerializedName("stickied")
        @Expose
        public Boolean stickied;
        @SerializedName("can_gild")
        @Expose
        public Boolean canGild;
        @SerializedName("subreddit")
        @Expose
        public String subreddit;
        @SerializedName("score_hidden")
        @Expose
        public Boolean scoreHidden;
        @SerializedName("subreddit_type")
        @Expose
        public String subredditType;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("created")
        @Expose
        public Integer created;
        @SerializedName("author_flair_text")
        @Expose
        public String authorFlairText;
        @SerializedName("created_utc")
        @Expose
        public Integer createdUtc;
        @SerializedName("subreddit_name_prefixed")
        @Expose
        public String subredditNamePrefixed;
        @SerializedName("controversiality")
        @Expose
        public Integer controversiality;
        @SerializedName("depth")
        @Expose
        public Integer depth;
        @SerializedName("mod_reports")
        @Expose
        public List<Object> modReports = null;
        @SerializedName("num_reports")
        @Expose
        public Object numReports;
        @SerializedName("distinguished")
        @Expose
        public Object distinguished;
        @SerializedName("count")
        @Expose
        public Integer count;
        @SerializedName("children")
        @Expose
        public List<Object> children = null;

    }


    public class Data____ {

        @SerializedName("modhash")
        @Expose
        public String modhash;
        @SerializedName("children")
        @Expose
        public List<Child__> children = null;
        @SerializedName("after")
        @Expose
        public Object after;
        @SerializedName("before")
        @Expose
        public Object before;

    }


    public class Data_____ {

        @SerializedName("subreddit_id")
        @Expose
        public String subredditId;
        @SerializedName("approved_at_utc")
        @Expose
        public Object approvedAtUtc;
        @SerializedName("banned_by")
        @Expose
        public Object bannedBy;
        @SerializedName("removal_reason")
        @Expose
        public Object removalReason;
        @SerializedName("link_id")
        @Expose
        public String linkId;
        @SerializedName("likes")
        @Expose
        public Object likes;
        @SerializedName("replies")
        @Expose
        public Replies__ replies;
        @SerializedName("user_reports")
        @Expose
        public List<Object> userReports = null;
        @SerializedName("saved")
        @Expose
        public Boolean saved;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("banned_at_utc")
        @Expose
        public Object bannedAtUtc;
        @SerializedName("gilded")
        @Expose
        public Integer gilded;
        @SerializedName("archived")
        @Expose
        public Boolean archived;
        @SerializedName("report_reasons")
        @Expose
        public Object reportReasons;
        @SerializedName("author")
        @Expose
        public String author;
        @SerializedName("can_mod_post")
        @Expose
        public Boolean canModPost;
        @SerializedName("ups")
        @Expose
        public Integer ups;
        @SerializedName("parent_id")
        @Expose
        public String parentId;
        @SerializedName("score")
        @Expose
        public Integer score;
        @SerializedName("approved_by")
        @Expose
        public Object approvedBy;
        @SerializedName("downs")
        @Expose
        public Integer downs;
        @SerializedName("body")
        @Expose
        public String body;
        @SerializedName("edited")
        @Expose
        public Boolean edited;
        @SerializedName("author_flair_css_class")
        @Expose
        public String authorFlairCssClass;
        @SerializedName("collapsed")
        @Expose
        public Boolean collapsed;
        @SerializedName("is_submitter")
        @Expose
        public Boolean isSubmitter;
        @SerializedName("collapsed_reason")
        @Expose
        public Object collapsedReason;
        @SerializedName("body_html")
        @Expose
        public String bodyHtml;
        @SerializedName("stickied")
        @Expose
        public Boolean stickied;
        @SerializedName("can_gild")
        @Expose
        public Boolean canGild;
        @SerializedName("subreddit")
        @Expose
        public String subreddit;
        @SerializedName("score_hidden")
        @Expose
        public Boolean scoreHidden;
        @SerializedName("subreddit_type")
        @Expose
        public String subredditType;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("created")
        @Expose
        public Integer created;
        @SerializedName("author_flair_text")
        @Expose
        public String authorFlairText;
        @SerializedName("created_utc")
        @Expose
        public Integer createdUtc;
        @SerializedName("subreddit_name_prefixed")
        @Expose
        public String subredditNamePrefixed;
        @SerializedName("controversiality")
        @Expose
        public Integer controversiality;
        @SerializedName("depth")
        @Expose
        public Integer depth;
        @SerializedName("mod_reports")
        @Expose
        public List<Object> modReports = null;
        @SerializedName("num_reports")
        @Expose
        public Object numReports;
        @SerializedName("distinguished")
        @Expose
        public Object distinguished;

    }


    public class Data______ {

        @SerializedName("modhash")
        @Expose
        public String modhash;
        @SerializedName("children")
        @Expose
        public List<Child___> children = null;
        @SerializedName("after")
        @Expose
        public Object after;
        @SerializedName("before")
        @Expose
        public Object before;

    }


    public class Data_______ {

        @SerializedName("subreddit_id")
        @Expose
        public String subredditId;
        @SerializedName("approved_at_utc")
        @Expose
        public Object approvedAtUtc;
        @SerializedName("banned_by")
        @Expose
        public Object bannedBy;
        @SerializedName("removal_reason")
        @Expose
        public Object removalReason;
        @SerializedName("link_id")
        @Expose
        public String linkId;
        @SerializedName("likes")
        @Expose
        public Object likes;
        @SerializedName("replies")
        @Expose
        public Replies___ replies;
        @SerializedName("user_reports")
        @Expose
        public List<Object> userReports = null;
        @SerializedName("saved")
        @Expose
        public Boolean saved;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("banned_at_utc")
        @Expose
        public Object bannedAtUtc;
        @SerializedName("gilded")
        @Expose
        public Integer gilded;
        @SerializedName("archived")
        @Expose
        public Boolean archived;
        @SerializedName("report_reasons")
        @Expose
        public Object reportReasons;
        @SerializedName("author")
        @Expose
        public String author;
        @SerializedName("can_mod_post")
        @Expose
        public Boolean canModPost;
        @SerializedName("ups")
        @Expose
        public Integer ups;
        @SerializedName("parent_id")
        @Expose
        public String parentId;
        @SerializedName("score")
        @Expose
        public Integer score;
        @SerializedName("approved_by")
        @Expose
        public Object approvedBy;
        @SerializedName("downs")
        @Expose
        public Integer downs;
        @SerializedName("body")
        @Expose
        public String body;
        @SerializedName("edited")
        @Expose
        public Integer edited;
        @SerializedName("author_flair_css_class")
        @Expose
        public String authorFlairCssClass;
        @SerializedName("collapsed")
        @Expose
        public Boolean collapsed;
        @SerializedName("is_submitter")
        @Expose
        public Boolean isSubmitter;
        @SerializedName("collapsed_reason")
        @Expose
        public Object collapsedReason;
        @SerializedName("body_html")
        @Expose
        public String bodyHtml;
        @SerializedName("stickied")
        @Expose
        public Boolean stickied;
        @SerializedName("can_gild")
        @Expose
        public Boolean canGild;
        @SerializedName("subreddit")
        @Expose
        public String subreddit;
        @SerializedName("score_hidden")
        @Expose
        public Boolean scoreHidden;
        @SerializedName("subreddit_type")
        @Expose
        public String subredditType;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("created")
        @Expose
        public Integer created;
        @SerializedName("author_flair_text")
        @Expose
        public String authorFlairText;
        @SerializedName("created_utc")
        @Expose
        public Integer createdUtc;
        @SerializedName("subreddit_name_prefixed")
        @Expose
        public String subredditNamePrefixed;
        @SerializedName("controversiality")
        @Expose
        public Integer controversiality;
        @SerializedName("depth")
        @Expose
        public Integer depth;
        @SerializedName("mod_reports")
        @Expose
        public List<Object> modReports = null;
        @SerializedName("num_reports")
        @Expose
        public Object numReports;
        @SerializedName("distinguished")
        @Expose
        public Object distinguished;
        @SerializedName("count")
        @Expose
        public Integer count;
        @SerializedName("children")
        @Expose
        public List<String> children = null;

    }


    public class Data________ {

        @SerializedName("modhash")
        @Expose
        public String modhash;
        @SerializedName("children")
        @Expose
        public List<Child____> children = null;
        @SerializedName("after")
        @Expose
        public Object after;
        @SerializedName("before")
        @Expose
        public Object before;

    }


    public class Data_________ {

        @SerializedName("count")
        @Expose
        public Integer count;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("parent_id")
        @Expose
        public String parentId;
        @SerializedName("depth")
        @Expose
        public Integer depth;
        @SerializedName("children")
        @Expose
        public List<String> children = null;

    }


    public class MediaEmbed {


    }


    public class Replies {

        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data__ data;

    }


    public class Replies_ {

        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data____ data;

    }


    public class Replies__ {

        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data______ data;

    }


    public class Replies___ {

        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data________ data;

    }


    public class SecureMediaEmbed {


    }
}
