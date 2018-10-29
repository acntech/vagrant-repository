import {
    Group,
    GroupState,
    GroupAction,
    CreateGroupAction,
    CreateGroupActionType,
    GetGroupAction,
    GetGroupActionType,
    FindGroupsAction,
    FindGroupsActionType
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
}

export const create = (state: GroupState = initialGroupState, action: CreateGroupAction): GroupState => {
    switch (action.type) {
        case CreateGroupActionType.LOADING: {
            const { loading } = action;
            return { ...state, loading: loading };
        }

        case CreateGroupActionType.SUCCESS: {
            const { payload } = action;
            let { groups } = state;

            if (payload) {
                groups = replaceOrAppend(groups, payload);
            }

            return { ...initialGroupState, groups: groups };
        }

        case CreateGroupActionType.ERROR: {
            const { error } = action;
            return { ...initialGroupState, error: error };
        }

        default: {
            return state;
        }
    }
}

export const get = (state: GroupState = initialGroupState, action: GetGroupAction): GroupState => {
    switch (action.type) {
        case GetGroupActionType.LOADING: {
            const { loading } = action;
            return { ...state, loading: loading };
        }

        case GetGroupActionType.SUCCESS: {
            const { payload } = action;
            let { groups } = state;

            if (payload) {
                groups = replaceOrAppend(groups, payload);
            }

            return { ...initialGroupState, groups: groups };
        }

        case GetGroupActionType.ERROR: {
            const { error } = action;
            return { ...initialGroupState, error: error };
        }

        default: {
            return state;
        }
    }
}

export const find = (state: GroupState = initialGroupState, action: FindGroupsAction): GroupState => {
    switch (action.type) {
        case FindGroupsActionType.LOADING: {
            const { loading } = action;
            return { ...state, loading: loading };
        }

        case FindGroupsActionType.SUCCESS: {
            const { payload } = action;
            let { groups } = state;
            if (payload) {
                payload.forEach(group => {
                    groups = replaceOrAppend(groups, group);
                });
            }
            return { ...initialGroupState, groups: payload };
        }

        case FindGroupsActionType.ERROR: {
            const { error } = action;
            return { ...initialGroupState, error: error };
        }

        default: {
            return state;
        }
    }
}

const replaceOrAppend = (groups: Group[], group: Group) => {
    const index = groups.map(group => group.id).indexOf(group.id);

    if (~index) {
        groups[index] = group;
    } else {
        groups = groups.concat(group);
    }

    return groups;
}