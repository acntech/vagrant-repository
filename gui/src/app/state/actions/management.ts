import axios from 'axios';

import {
    ManagementInfo,
    GetManagementInfoActionType,
    GetManagementInfoErrorAction,
    GetManagementInfoLoadingAction,
    GetManagementInfoSuccessAction,
} from '../../models';

const getManagementInfoLoading = (loading: boolean): GetManagementInfoLoadingAction => ({ type: GetManagementInfoActionType.LOADING, loading });
const getManagementInfoSuccess = (payload: ManagementInfo): GetManagementInfoSuccessAction => ({ type: GetManagementInfoActionType.SUCCESS, payload });
const getManagementInfoError = (error: any): GetManagementInfoErrorAction => ({ type: GetManagementInfoActionType.ERROR, error });

const managementRootPath = '/api/management';

export function getManagementInfo() {
    return (dispatch) => {
        dispatch(getManagementInfoLoading(true));
        const url = `${managementRootPath}/info`;
        return axios.get(url)
            .then((response) => {
                return dispatch(getManagementInfoSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(getManagementInfoError(error));
            });
    };
}
