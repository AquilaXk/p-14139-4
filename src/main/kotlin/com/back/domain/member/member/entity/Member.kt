package com.back.domain.member.member.entity

import com.back.global.jpa.domain.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
class Member(
    @Column(unique = true, length = 60, nullable = false)
    var username: String,
    @Column(length = 65, nullable = false)
    var password: String,
    @Column(length = 30, nullable = false)
    var nickname: String
) : BaseTime()