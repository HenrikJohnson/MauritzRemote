package com.underscoreresearch.mauritzremote.library;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by henri on 1/29/2017.
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class QueueItem extends LibraryItem
{
    public int queueId;
}
