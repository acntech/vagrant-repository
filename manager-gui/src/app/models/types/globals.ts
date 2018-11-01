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
    domain: string;
    timestamp: string;
    status: number;
    error: string;
    message: string;
    path: string;
}

export interface Notice {
    severity: 'info' | 'warning' | 'error';
    header: string;
    content?: string | ReactNode;
}

export interface Modified {
    id: number;
}