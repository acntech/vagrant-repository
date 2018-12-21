import {
    CreateGroupActionType,
    DeleteGroupActionType,
    FindGroupsActionType,
    GetGroupActionType,
    UpdateGroupActionType,
    Error,
    Modified
} from '../';

export interface ModifyGroup {
    name: string;
    description?: string;
}

export interface Group {
    id: number;
    name: string;
    description: string;
}

export interface GroupState {
    loading: boolean;
    groups: Group[];
    error?: Error;
    modified?: Modified;
}

export interface CreateGroupLoadingAction {
    type: CreateGroupActionType.LOADING,
    loading: boolean
}

export interface CreateGroupSuccessAction {
    type: CreateGroupActionType.SUCCESS,
    payload: Group
}

export interface CreateGroupErrorAction {
    type: CreateGroupActionType.ERROR,
    error: any
}

export interface DeleteGroupLoadingAction {
    type: DeleteGroupActionType.LOADING,
    loading: boolean
}

export interface DeleteGroupSuccessAction {
    type: DeleteGroupActionType.SUCCESS,
    groupId: number
}

export interface DeleteGroupErrorAction {
    type: DeleteGroupActionType.ERROR,
    error: any
}

export interface FindGroupsLoadingAction {
    type: FindGroupsActionType.LOADING,
    loading: boolean
}

export interface FindGroupsSuccessAction {
    type: FindGroupsActionType.SUCCESS,
    payload: Group[]
}

export interface FindGroupsErrorAction {
    type: FindGroupsActionType.ERROR,
    error: any
}

export interface GetGroupLoadingAction {
    type: GetGroupActionType.LOADING,
    loading: boolean
}

export interface GetGroupSuccessAction {
    type: GetGroupActionType.SUCCESS,
    payload: Group
}

export interface GetGroupErrorAction {
    type: GetGroupActionType.ERROR,
    error: any
}

export interface UpdateGroupLoadingAction {
    type: UpdateGroupActionType.LOADING,
    loading: boolean
}

export interface UpdateGroupSuccessAction {
    type: UpdateGroupActionType.SUCCESS,
    payload: Group
}

export interface UpdateGroupErrorAction {
    type: UpdateGroupActionType.ERROR,
    error: any
}

export type CreateGroupAction = CreateGroupLoadingAction | CreateGroupSuccessAction | CreateGroupErrorAction;
export type DeleteGroupAction = DeleteGroupLoadingAction | DeleteGroupSuccessAction | DeleteGroupErrorAction;
export type FindGroupsAction = FindGroupsLoadingAction | FindGroupsSuccessAction | FindGroupsErrorAction;
export type GetGroupAction = GetGroupLoadingAction | GetGroupSuccessAction | GetGroupErrorAction;
export type UpdateGroupAction = UpdateGroupLoadingAction | UpdateGroupSuccessAction | UpdateGroupErrorAction;

export type GroupAction = CreateGroupAction | DeleteGroupAction | FindGroupsAction | GetGroupAction | UpdateGroupAction;