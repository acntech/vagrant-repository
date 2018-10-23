import {
    GroupState,
    GroupAction,
    GetGroupAction,
    GetGroupActionType,
    FindGroupsAction,
    FindGroupsActionType,
    initialGroupState
} from '../../models';

export function reducer(state: GroupState = initialGroupState, action: GroupAction): GroupState {
    switch (action.type) {
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

export function get(state: GroupState = initialGroupState, action: GetGroupAction): GroupState {
    switch (action.type) {
        case GetGroupActionType.LOADING: {
            const {loading} = action;
            return {...state, loading: loading};
        }

        case GetGroupActionType.SUCCESS: {
            const {payload} = action;
            let {groups} = state;

            if (payload) {
                const {id: payloadId} = payload;
                const group = groups.find(group => group.id === payloadId);
                if (!group) {
                    groups = groups.concat(payload);
                }
            }

            return {...initialGroupState, groups: groups};
        }

        case GetGroupActionType.ERROR: {
            const {error} = action;
            return {...initialGroupState, error: error};
        }

        default: {
            return state;
        }
    }
}

export function find(state: GroupState = initialGroupState, action: FindGroupsAction): GroupState {
    switch (action.type) {
        case FindGroupsActionType.LOADING: {
            const {loading} = action;
            return {...state, loading: loading};
        }

        case FindGroupsActionType.SUCCESS: {
            const {payload} = action;
            let {groups} = state;
            if (payload) {
                payload.forEach(group => {
                    let index = groups.indexOf(group);
                    if (~index) {
                        groups[index] = group;
                    } else {
                        groups = groups.concat(payload);
                    }
                });
            }
            return {...initialGroupState, groups: payload};
        }

        case FindGroupsActionType.ERROR: {
            const {error} = action;
            return {...initialGroupState, error: error};
        }

        default: {
            return state;
        }
    }
}