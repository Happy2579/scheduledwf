package net.jas34.scheduledwf.dao.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Singleton;
import net.jas34.scheduledwf.dao.ScheduledWfExecutionDAO;
import net.jas34.scheduledwf.run.ScheduledWorkFlow;

/**
 * @author Jasbir Singh
 */
@Singleton
public class InMemoryScheduledWfExecutionDAO implements ScheduledWfExecutionDAO {

    private final Map<String, ScheduledWorkFlow> scheduledWorkFlowStore = new ConcurrentHashMap<>();

    @Override
    public String createScheduledWorkflow(ScheduledWorkFlow scheduledWorkFlow) {
        scheduledWorkFlowStore.put(scheduledWorkFlow.getName(), scheduledWorkFlow);
        return scheduledWorkFlow.getId();
    }

    @Override
    public Optional<ScheduledWorkFlow> getScheduledWfWithNameAndMgrRefId(String name, String managerRefId) {
        return Optional.ofNullable(scheduledWorkFlowStore.get(name));
    }

    @Override
    public Optional<List<ScheduledWorkFlow>> getAllScheduledWfWithByManagerRefId(String managerRefId) {
        return Optional.of(new ArrayList<>(scheduledWorkFlowStore.values()));
    }

    @Override
    public Optional<List<ScheduledWorkFlow>> getAllScheduledWfWithStates(String managerRefId,
            ScheduledWorkFlow.State... states) {
        List<ScheduledWorkFlow> collect = scheduledWorkFlowStore.values().stream()
                .filter(scheduledWorkFlow -> Stream.of(states)
                        .collect(Collectors.toCollection(ArrayList::new))
                        .contains(scheduledWorkFlow.getState()))
                .collect(Collectors.toList());
        return Optional.of(collect);
    }

    @Override
    public ScheduledWorkFlow updateStateById(ScheduledWorkFlow.State state, String id, String name) {
        ScheduledWorkFlow scheduledWorkFlow = scheduledWorkFlowStore.get(name);
        if(Objects.isNull(scheduledWorkFlow)) {
            return null;
        }
        synchronized (scheduledWorkFlowStore) {
            scheduledWorkFlow.setState(state);
            return scheduledWorkFlow;
        }
    }

    @Override
    public void removeAllScheduledWorkflows(String managerRefId) {
        scheduledWorkFlowStore.clear();
    }
}