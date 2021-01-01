package com.siliconst.ndvassistant.NetworkResponses;

import com.siliconst.ndvassistant.Models.Department;
import com.siliconst.ndvassistant.Models.Notice;
import com.siliconst.ndvassistant.Models.Reply;
import com.siliconst.ndvassistant.Models.Ticket;
import com.siliconst.ndvassistant.Models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("tickets")
    @Expose
    private List<Ticket> tickets = null;
    @SerializedName("ticket")
    @Expose
    private Ticket ticket = null;
    @SerializedName("departments")
    @Expose
    private List<Department> departments = null;
    @SerializedName("notices")
    @Expose
    private List<Notice> notices = null;
    @SerializedName("replies")
    @Expose
    private List<Reply> replies = null;

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    public List<Notice> getNotices() {
        return notices;
    }

    public void setNotices(List<Notice> notices) {
        this.notices = notices;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
