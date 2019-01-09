import {
    ManagementState,
    ManagementAction,
    GetManagementInfoAction,
    GetManagementInfoActionType,
    EntityType,
    ActionType
} from '../../models';
import { initialManagementState } from '../store/initial-state';

export const reducer = (state: ManagementState = initialManagementState, action: ManagementAction): ManagementState => {
    switch (action.type) {
        case GetManagementInfoActionType.LOADING:
        case GetManagementInfoActionType.SUCCESS:
        case GetManagementInfoActionType.ERROR:
            return getGroup(state, action);
        default:
            return state;
    }
};

const getGroup = (state: ManagementState = initialManagementState, action: GetManagementInfoAction): ManagementState => {
    switch (action.type) {
        case GetManagementInfoActionType.LOADING: {
            const { info } = state;
            const { loading } = action;
            return { ...initialManagementState, info: info, loading: loading };
        }

        case GetManagementInfoActionType.SUCCESS: {
            const { payload } = action;
            return { ...initialManagementState, info: payload };
        }

        case GetManagementInfoActionType.ERROR: {
            const { info } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.MANAGEMENT, actionType: ActionType.GET };
            return { ...initialManagementState, info: info, error: error };
        }

        default: {
            return state;
        }
    }
};
