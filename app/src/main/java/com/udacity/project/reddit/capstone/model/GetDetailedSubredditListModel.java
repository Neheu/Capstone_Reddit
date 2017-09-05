package com.udacity.project.reddit.capstone.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Neha on 01-09-2017.
 */

public class GetDetailedSubredditListModel {

    public class Child {

        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data_ data;

    }



    @SerializedName("data")
    @Expose
    public Data data;
    @SerializedName("kind")
    @Expose
    public String kind;




    public class Data
    {
        @SerializedName("modhash")
        @Expose
        public String modhash;
        @SerializedName("children")
        @Expose
        public List<Child> children = null;
        @SerializedName("after")
        @Expose
        public String after;
        @SerializedName("before")
        @Expose
        public Object before;
    }

    public class Data_ {
        public int subs_count;
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
        public Integer thumbnailWidth;
        @SerializedName("subreddit")
        @Expose
        public String subreddit;
        @SerializedName("selftext_html")
        @Expose
        public Object selftextHtml;
        @SerializedName("selftext")
        @Expose
        public String selftext;
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
        @SerializedName("mod_reports")
        @Expose
        public List<Object> modReports = null;
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
        @SerializedName("preview")
        @Expose
        public Preview preview;
        @SerializedName("thumbnail")
        @Expose
        public String thumbnail;
        @SerializedName("subreddit_id")
        @Expose
        public String subredditId;
//        @SerializedName("edited")
//        @Expose
//        public Boolean edited;
        @SerializedName("link_flair_css_class")
        @Expose
        public String linkFlairCssClass;
        @SerializedName("author_flair_css_class")
        @Expose
        public Object authorFlairCssClass;
        @SerializedName("contest_mode")
        @Expose
        public Boolean contestMode;
        @SerializedName("gilded")
        @Expose
        public Integer gilded;
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
        @SerializedName("post_hint")
        @Expose
        public String postHint;
        @SerializedName("author_flair_text")
        @Expose
        public Object authorFlairText;
        @SerializedName("stickied")
        @Expose
        public Boolean stickied;
        @SerializedName("can_gild")
        @Expose
        public Boolean canGild;
        @SerializedName("thumbnail_height")
        @Expose
        public Integer thumbnailHeight;
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
        @SerializedName("subreddit_type")
        @Expose
        public String subredditType;
        @SerializedName("locked")
        @Expose
        public Boolean locked;
        @SerializedName("hide_score")
        @Expose
        public Boolean hideScore;
        @SerializedName("created")
        @Expose
        public Integer created;
        @SerializedName("url")
        @Expose
        public String url;
        @SerializedName("whitelist_status")
        @Expose
        public String whitelistStatus;
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
        @SerializedName("ups")
        @Expose
        public Integer ups;
        @SerializedName("media")
        @Expose
        public Object media;
        @SerializedName("num_comments")
        @Expose
        public Integer numComments;
        @SerializedName("is_self")
        @Expose
        public Boolean isSelf;
        @SerializedName("visited")
        @Expose
        public Boolean visited;
        @SerializedName("num_reports")
        @Expose
        public Object numReports;
        @SerializedName("is_video")
        @Expose
        public Boolean isVideo;
        @SerializedName("distinguished")
        @Expose
        public Object distinguished;

    }




    public class Image {

        @SerializedName("source")
        @Expose
        public Source source;
        @SerializedName("resolutions")
        @Expose
        public List<Object> resolutions = null;
        @SerializedName("variants")
        @Expose
        public Variants variants;
        @SerializedName("id")
        @Expose
        public String id;

    }


    public class MediaEmbed {


    }

    public class Preview {

        @SerializedName("images")
        @Expose
        public List<Image> images = null;
        @SerializedName("enabled")
        @Expose
        public Boolean enabled;

    }


    public class SecureMediaEmbed {


    }

    public class Source {

        @SerializedName("thumb_url")
        @Expose
        public String url;
        @SerializedName("width")
        @Expose
        public Integer width;
        @SerializedName("height")
        @Expose
        public Integer height;

    }


    public class Variants {


    }
}
