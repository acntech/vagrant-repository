import { ReactNode } from 'react';
import { IntlState } from 'react-intl-redux';
import { BoxState, GroupState, ProviderState, VersionState } from './';

export interface RootState {
    intl: IntlState;
    groupState: GroupState;
    boxState: BoxState;
    versionState: VersionState;
    providerState: ProviderState;
}

export interface Error {
    entityType: EntityType;
    actionType: ActionType;
    timestamp: string;
    status: number;
    error: string;
    message: string;
    path: string;
}

export interface Notice {
    severity: 'info' | 'warning' | 'error' | 'success';
    header: string;
    content?: string | ReactNode;
}

export interface Modified {
    id: number;
    entityType: EntityType;
    actionType: ActionType;
}

export enum EntityType {
    GROUP = 'group',
    BOX = 'box',
    VERSION = 'version',
    PROVIDER = 'provider'
}

export enum ActionType {
    GET = 'get',
    FIND = 'find',
    CREATE = 'create',
    UPDATE = 'update',
    DELETE = 'delete'
}