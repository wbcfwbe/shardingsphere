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

package org.apache.shardingsphere.shadow.subscriber;

import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.infra.config.rule.RuleConfiguration;
import org.apache.shardingsphere.infra.instance.InstanceContext;
import org.apache.shardingsphere.infra.metadata.database.ShardingSphereDatabase;
import org.apache.shardingsphere.infra.rule.RuleConfigurationSubscribeCoordinator;
import org.apache.shardingsphere.mode.event.config.DatabaseRuleConfigurationChangedEvent;
import org.apache.shardingsphere.shadow.api.config.ShadowRuleConfiguration;
import org.apache.shardingsphere.shadow.api.config.table.ShadowTableConfiguration;
import org.apache.shardingsphere.shadow.event.table.AddShadowTableEvent;
import org.apache.shardingsphere.shadow.event.table.AlterShadowTableEvent;
import org.apache.shardingsphere.shadow.event.table.DeleteShadowTableEvent;
import org.apache.shardingsphere.shadow.rule.ShadowRule;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Shadow table subscriber.
 */
@SuppressWarnings("UnstableApiUsage")
@RequiredArgsConstructor
public final class ShadowTableSubscriber implements RuleConfigurationSubscribeCoordinator {
    
    private Map<String, ShardingSphereDatabase> databases;
    
    private InstanceContext instanceContext;
    
    @Override
    public void registerRuleConfigurationSubscriber(final Map<String, ShardingSphereDatabase> databases, final InstanceContext instanceContext) {
        this.databases = databases;
        this.instanceContext = instanceContext;
        instanceContext.getEventBusContext().register(this);
    }
    
    /**
     * Renew with add table.
     *
     * @param event add table event
     */
    @Subscribe
    public synchronized void renew(final AddShadowTableEvent<ShadowTableConfiguration> event) {
        renew(event.getDatabaseName(), event.getTableName(), event.getConfig());
    }
    
    /**
     * Renew with alter table.
     *
     * @param event alter table event
     */
    @Subscribe
    public synchronized void renew(final AlterShadowTableEvent<ShadowTableConfiguration> event) {
        renew(event.getDatabaseName(), event.getTableName(), event.getConfig());
    }
    
    private void renew(final String databaseName, final String tableName, final ShadowTableConfiguration tableConfig) {
        ShardingSphereDatabase database = databases.get(databaseName);
        Collection<RuleConfiguration> ruleConfigs = new LinkedList<>(database.getRuleMetaData().getConfigurations());
        ShadowRuleConfiguration config = (ShadowRuleConfiguration) database.getRuleMetaData().getSingleRule(ShadowRule.class).getConfiguration();
        config.getTables().put(tableName, tableConfig);
        database.getRuleMetaData().getConfigurations().addAll(ruleConfigs);
        instanceContext.getEventBusContext().post(new DatabaseRuleConfigurationChangedEvent(databaseName, config));
    }
    
    /**
     * Renew with delete table.
     *
     * @param event delete table event
     */
    @Subscribe
    public synchronized void renew(final DeleteShadowTableEvent event) {
        ShardingSphereDatabase database = databases.get(event.getDatabaseName());
        Collection<RuleConfiguration> ruleConfigs = new LinkedList<>(database.getRuleMetaData().getConfigurations());
        ShadowRuleConfiguration config = (ShadowRuleConfiguration) database.getRuleMetaData().getSingleRule(ShadowRule.class).getConfiguration();
        config.getTables().remove(event.getTableName());
        database.getRuleMetaData().getConfigurations().addAll(ruleConfigs);
        instanceContext.getEventBusContext().post(new DatabaseRuleConfigurationChangedEvent(event.getDatabaseName(), config));
    }
}
