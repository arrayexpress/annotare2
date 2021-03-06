/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.db.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "submission_feedback")
public class SubmissionFeedback {
    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "posted", nullable = false)
    private Date posted;

    @Column(name = "score")
    private Byte score;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submissionId", nullable = false)
    private Submission submission;

    public SubmissionFeedback() {
        this(null, null);
    }

    public SubmissionFeedback(Submission submission, Byte score) {
        this.posted = new Date();
        this.submission = submission;
        this.score = score;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getPosted() {
        return posted;
    }

    public int getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }
}
