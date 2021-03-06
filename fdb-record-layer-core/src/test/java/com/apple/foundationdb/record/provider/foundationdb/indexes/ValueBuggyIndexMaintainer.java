/*
 * ValueBuggyIndexMaintainer.java
 *
 * This source file is part of the FoundationDB open source project
 *
 * Copyright 2015-2018 Apple Inc. and the FoundationDB project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apple.foundationdb.record.provider.foundationdb.indexes;

import com.apple.foundationdb.record.IndexEntry;
import com.apple.foundationdb.record.provider.foundationdb.FDBIndexableRecord;
import com.apple.foundationdb.record.provider.foundationdb.IndexMaintainerState;
import com.google.protobuf.Message;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A {@link com.apple.foundationdb.record.provider.foundationdb.IndexMaintainer} that occasionally fails.
 */
public class ValueBuggyIndexMaintainer extends ValueIndexMaintainer {
    public ValueBuggyIndexMaintainer(IndexMaintainerState state) {
        super(state);
    }

    @Override
    protected <M extends Message> CompletableFuture<Void> updateIndexKeys(@Nonnull FDBIndexableRecord<M> savedRecord,
                                                                          boolean remove,
                                                                          @Nonnull List<IndexEntry> indexEntries) {

        return super.updateIndexKeys(savedRecord, remove, indexEntries).thenApply( vignore -> {
            int num = ((Number)indexEntries.get(0).getKey().get(0)).intValue();
            if (remove && num % 2 == 0) {
                throw new UnsupportedOperationException("Cannot remove index key beginning with even number");
            } else if (!remove && num % 2 != 0) {
                throw new UnsupportedOperationException("Cannot add index key beginning with odd number");
            } else {
                return null;
            }
        });
    }
}
