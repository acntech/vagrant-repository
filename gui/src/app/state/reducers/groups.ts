import {
    Group,
    GroupState,
    GroupAction,
    CreateGroupAction,
    CreateGroupActionType,
    GetGroupAction,
    GetGroupActionType,
    FindGroupsAction,
    FindGroupsActionType,
    EntityType,
    ActionType
} from '../../models';
import { initialGroupState } from '../store/initial-state';

export const reducer = (state: GroupState = initialGroupState, action: GroupAction): GroupState => {
    switch (action.type) {
        case CreateGroupActionType.LOADING:
        case CreateGroupActionType.SUCCESS:
        case CreateGroupActionType.ERROR:
            return create(state, action);
        case GetGroupActionType.LOADING:
        case GetGroupActionType.SUCCESS:
        case GetGroupActionType.ERROR:
            return get(state, action);
        case FindGroupsActionType.LOADING:
        case FindGroupsActionType.SUCCESS:
        case FindGroupsActionType.ERROR:
            return find(state, action);
        default:
            return state;
    }
};

const create = (state: GroupState = initialGroupState, action: CreateGroupAction): GroupState => {
    switch (action.type) {
        case CreateGroupActionType.LOADING: {
            const { groups } = state;
            const { loading } = action;
            return { ...initialGroupState, groups: groups, loading: loading };
        }

        case CreateGroupActionType.SUCCESS: {
            let { groups } = state;
            const { payload } = action;
            let modified;

            if (payload) {
                groups = replaceOrAppend(groups, payload);
                modified = { id: payload.id, entityType: EntityType.GROUP, actionType: ActionType.CREATE };
            }

            return { ...initialGroupState, groups: groups, modified: modified };
        }

        case CreateGroupActionType.ERROR: {
            const { groups } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.GROUP, actionType: ActionType.CREATE };
            return { ...initialGroupState, groups: groups, error: error };
        }

        default: {
            return state;
        }
    }
};

const get = (state: GroupState = initialGroupState, action: GetGroupAction): GroupState => {
    switch (action.type) {
        case GetGroupActionType.LOADING: {
            const { groups } = state;
            const { loading } = action;
            return { ...initialGroupState, groups: groups, loading: loading };
        }

        case GetGroupActionType.SUCCESS: {
            let { groups } = state;
            const { payload } = action;

            if (payload) {
                groups = replaceOrAppend(groups, payload);
            }

            return { ...initialGroupState, groups: groups };
        }

        case GetGroupActionType.ERROR: {
            const { groups } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.GROUP, actionType: ActionType.GET };
            return { ...initialGroupState, groups: groups, error: error };
        }

        default: {
            return state;
        }
    }
};

const find = (state: GroupState = initialGroupState, action: FindGroupsAction): GroupState => {
    switch (action.type) {
        case FindGroupsActionType.LOADING: {
            const { groups } = state;
            const { loading } = action;
            return { ...initialGroupState, groups: groups, loading: loading };
        }

        case FindGroupsActionType.SUCCESS: {
            let { groups } = state;
            const { payload } = action;

            if (payload) {
                payload.forEach(group => {
                    groups = replaceOrAppend(groups, group);
                });
            }

            return { ...initialGroupState, groups: groups };
        }

        case FindGroupsActionType.ERROR: {
            const { groups } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.GROUP, actionType: ActionType.FIND };
            return { ...initialGroupState, groups: groups, error: error };
        }

        default: {
            return state;
        }
    }
};

const replaceOrAppend = (groups: Group[], group: Group) => {
    const index = groups.map(group => group.id).indexOf(group.id);

    if (~index) {
        groups[index] = group;
    } else {
        groups = groups.concat(group);
    }

    return groups;
};