package com.udacity.project.reddit.capstone.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.udacity.project.reddit.capstone.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Neha on 04-09-2017.
 */

public class GetCommentsModel {
    @SerializedName("kind")
    @Expose
    public String kind;
    @SerializedName("data")
    @Expose
    public Data data;


    public class Child {

        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data_ data;

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


        //        @SerializedName("domain")
//        @Expose
//        public String domain;
//        @SerializedName("approved_at_utc")
//        @Expose
//        public Object approvedAtUtc;
//        @SerializedName("banned_by")
//        @Expose
//        public Object bannedBy;
//        @SerializedName("media_embed")
//        @Expose
//        public MediaEmbed mediaEmbed;
//        @SerializedName("thumbnail_width")
//        @Expose
//        public Object thumbnailWidth;
        @SerializedName("subreddit")
        @Expose
        public String subreddit;
        //        @SerializedName("selftext_html")
//        @Expose
//        public String selftextHtml;
        @SerializedName("selftext")
        @Expose
        public String selftext;
        //        @SerializedName("likes")
//        @Expose
//        public Object likes;
//        @SerializedName("suggested_sort")
//        @Expose
//        public String suggestedSort;
//        @SerializedName("user_reports")
//        @Expose
//        public List<Object> userReports = null;
//        @SerializedName("secure_media")
//        @Expose
//        public Object secureMedia;
//        @SerializedName("link_flair_text")
//        @Expose
//        public Object linkFlairText;
        @SerializedName("id")
        @Expose
        public String id;
        public Reply mReplyData;

        public void setReply(Reply rep) throws JSONException {

            mReplyData = rep;

        }

        //        @SerializedName("banned_at_utc")
//        @Expose
//        public Object bannedAtUtc;
//        @SerializedName("view_count")
//        @Expose
//        public Object viewCount;
//        @SerializedName("archived")
//        @Expose
//        public Boolean archived;
//        @SerializedName("clicked")
//        @Expose
//        public Boolean clicked;
//        @SerializedName("report_reasons")
//        @Expose
//        public Object reportReasons;
        @SerializedName("title")
        @Expose
        public String title;
        //        @SerializedName("num_crossposts")
//        @Expose
//        public Integer numCrossposts;
//        @SerializedName("saved")
//        @Expose
//        public Boolean saved;
//        @SerializedName("can_mod_post")
//        @Expose
//        public Boolean canModPost;
//        @SerializedName("is_crosspostable")
//        @Expose
//        public Boolean isCrosspostable;
        @SerializedName("score")
        @Expose
        public Integer score;
        //        @SerializedName("approved_by")
//        @Expose
//        public Object approvedBy;
//        @SerializedName("over_18")
//        @Expose
//        public Boolean over18;
//        @SerializedName("hidden")
//        @Expose
//        public Boolean hidden;
        @SerializedName("num_comments")
        @Expose
        public Integer numComments;
        //        @SerializedName("thumbnail")
//        @Expose
//        public String thumbnail;
        @SerializedName("subreddit_id")
        @Expose
        public String subredditId;
        //        @SerializedName("hide_score")
//        @Expose
//        public Boolean hideScore;
//        @SerializedName("edited")
//        @Expose
//        public Boolean edited;
//        @SerializedName("link_flair_css_class")
//        @Expose
//        public Object linkFlairCssClass;
//        @SerializedName("author_flair_css_class")
//        @Expose
//        public Object authorFlairCssClass;
//        @SerializedName("contest_mode")
//        @Expose
//        public Boolean contestMode;
//        @SerializedName("gilded")
//        @Expose
//        public Integer gilded;
//        @SerializedName("locked")
//        @Expose
//        public Boolean locked;
        @SerializedName("downs")
        @Expose
        public Integer downs;
        //        @SerializedName("brand_safe")
//        @Expose
//        public Boolean brandSafe;
//        @SerializedName("secure_media_embed")
//        @Expose
//        public SecureMediaEmbed secureMediaEmbed;
//        @SerializedName("removal_reason")
//        @Expose
//        public Object removalReason;
//        @SerializedName("can_gild")
//        @Expose
//        public Boolean canGild;
//        @SerializedName("thumbnail_height")
//        @Expose
//        public Object thumbnailHeight;
//        @SerializedName("parent_whitelist_status")
//        @Expose
//        public String parentWhitelistStatus;
        @SerializedName("name")
        @Expose
        public String name;
        //        @SerializedName("spoiler")
//        @Expose
//        public Boolean spoiler;
//        @SerializedName("permalink")
//        @Expose
//        public String permalink;
//        @SerializedName("num_reports")
//        @Expose
//        public Object numReports;
//        @SerializedName("whitelist_status")
//        @Expose
//        public String whitelistStatus;
//        @SerializedName("stickied")
//        @Expose
//        public Boolean stickied;
        @SerializedName("created")
        @Expose
        public Integer created;
        @SerializedName("url")
        @Expose
        public String url;
        //        @SerializedName("author_flair_text")
//        @Expose
//        public Object authorFlairText;
//        @SerializedName("quarantine")
//        @Expose
//        public Boolean quarantine;
        @SerializedName("author")
        @Expose
        public String author;
        //        @SerializedName("created_utc")
//        @Expose
//        public Integer createdUtc;
        @SerializedName("subreddit_name_prefixed")
        @Expose
        public String subredditNamePrefixed;
        //        @SerializedName("distinguished")
//        @Expose
//        public Object distinguished;
//        @SerializedName("media")
//        @Expose
//        public Object media;
//        @SerializedName("upvote_ratio")
//        @Expose
//        public Double upvoteRatio;
//        @SerializedName("mod_reports")
//        @Expose
//        public List<Object> modReports = null;
//        @SerializedName("is_self")
//        @Expose
//        public Boolean isSelf;
//        @SerializedName("visited")
//        @Expose
//        public Boolean visited;
        @SerializedName("subreddit_type")
        @Expose
        public String subredditType;
        //        @SerializedName("is_video")
//        @Expose
//        public Boolean isVideo;
        @SerializedName("ups")
        @Expose
        public Integer ups;
        @SerializedName("link_id")
        @Expose
        public String linkId;
        //        @SerializedName("replies")
//        @Expose
        // public Reply replies;
        @SerializedName("parent_id")
        @Expose
        public String parentId;
        @SerializedName("body")
        @Expose
        public String body;
        //        @SerializedName("collapsed")
//        @Expose
//        public Boolean collapsed;
//        @SerializedName("is_submitter")
//        @Expose
//        public Boolean isSubmitter;
//        @SerializedName("collapsed_reason")
//        @Expose
//        public Object collapsedReason;
//        @SerializedName("body_html")
//        @Expose
//        public String bodyHtml;
//        @SerializedName("score_hidden")
//        @Expose
//        public Boolean scoreHidden;
//        @SerializedName("controversiality")
//        @Expose
//        public Integer controversiality;
        @SerializedName("depth")
        @Expose
        public Integer depth;

    }


    public class MediaEmbed {


    }


    public class SecureMediaEmbed {


    }

    public class Reply {
        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data data;
    }
    static Retrofit retrofit = null;
    public static Retrofit getClient() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GetCommentsModel.Data_.class, new GetCommentsModel.ReplyDeserializer())
                .setLenient()
                .create();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
    public static class ReplyDeserializer implements JsonDeserializer<GetCommentsModel.Data_> {
        GetCommentsModel.Data_ replyState;

        @Override
        public GetCommentsModel.Data_ deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                replyState = new Gson().fromJson(json, GetCommentsModel.Data_.class);
                JsonObject jsonObject = json.getAsJsonObject();
                if (jsonObject.has("replies")) {
                    JsonElement elem = jsonObject.get("replies");

                    if (elem != null && !elem.isJsonNull()) {
                        if (elem.isJsonObject()) {
                           Reply rep = new Gson().fromJson(elem, GetCommentsModel.Reply.class);
                           // Reply rep = context.deserialize(jsonObject.get("replies"), GetCommentsModel.Reply.class);
                            replyState.setReply(rep);
                        } else {
                            replyState.setReply(null);
                        }
                    }
                }

            } catch (Exception exp) {
                String e = exp.toString();
            }
            return replyState;
        }
    }
}
