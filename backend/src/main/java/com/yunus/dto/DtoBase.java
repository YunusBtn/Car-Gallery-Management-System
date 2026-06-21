package com.yunus.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class DtoBase implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private LocalDateTime createTime;

	private LocalDateTime updateTime;
}
