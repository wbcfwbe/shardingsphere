/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.rule.identifier.type;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Table names mapper.
 */
@RequiredArgsConstructor
public final class TableNamesMapper {
    
    private final Map<String, String> lowerCaseTableNames = new LinkedHashMap<>();
    
    public TableNamesMapper(final Collection<String> tableNames) {
        tableNames.forEach(each -> lowerCaseTableNames.put(each.toLowerCase(), each));
    }
    
    /**
     * Judge whether contains table or not.
     * 
     * @param tableName table name
     * @return whether contains table or not
     */
    public boolean contains(final String tableName) {
        return lowerCaseTableNames.containsKey(tableName.toLowerCase());
    }
    
    /**
     * Get original table names.
     * 
     * @return original table names
     */
    public Collection<String> getTableNames() {
        return lowerCaseTableNames.values();
    }
    
    /**
     * Put.
     * 
     * @param tableName table name
     */
    public void put(final String tableName) {
        lowerCaseTableNames.put(tableName.toLowerCase(), tableName);
    }
    
    /**
     * Remove.
     * 
     * @param tableName table name
     */
    public void remove(final String tableName) {
        lowerCaseTableNames.remove(tableName.toLowerCase());
    }
}
