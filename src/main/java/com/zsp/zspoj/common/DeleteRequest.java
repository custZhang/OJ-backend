package com.zsp.zspoj.common;

import java.io.Serializable;
import lombok.Data;

/**
 * 删除请求
 *
   * @author <a href="https://zsp2024.cn">和风</a>
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}