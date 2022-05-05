package com.game.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "player")
public class Player {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="name")
    private String name;

    @Column(name="title")
    private String title;
    @Column(name="race")
    private String race;
    @Column(name="profession")
    private String profession;
    @Column(name="birthday")
    private Date birthday;
    @Column(name="banned")
    private Boolean banned;
    @Column(name="experience")
    private Integer experience;
    @Column(name="level")
    private Integer level;
    @Column(name = "untilnextlevel")
    private Integer untilNextLevel;

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Integer untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }



    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getRace() {
        return race;
    }

    public String getProfession() {
        return profession;
    }



    public Boolean isBanned() {
        return banned;
    }

    public Integer getExperience() {
        return experience;
    }

    public Integer getLevel() {
        return level;
    }


}
