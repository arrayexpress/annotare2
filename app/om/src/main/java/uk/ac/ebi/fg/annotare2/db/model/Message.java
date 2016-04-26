package uk.ac.ebi.fg.annotare2.db.model;

import uk.ac.ebi.fg.annotare2.db.model.enums.MessageStatus;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "messages")

public class Message {

    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent")
    private Date sent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MessageStatus status;

    @Column(name = "`from`", nullable = false)
    private String from;

    @Column(name = "`to`", nullable = false)
    private String to;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "body", nullable = false)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submissionId")
    private Submission submission;

    public Message() {
        this("", "", "", "");
    }

    public Message(String from, String to, String subject, String body) {
        created = new Date();
        status = MessageStatus.QUEUED;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Date getCreated() {
        return created;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message otherMsg = (Message) o;

        return id != null ? id.equals(otherMsg.id) : otherMsg.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
