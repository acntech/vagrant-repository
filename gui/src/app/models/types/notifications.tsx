import { ReactNode } from 'react';
import { NotificationActionType } from '../';

export interface ShowNotification {
    severity: 'info' | 'warning' | 'error' | 'success';
    title: string;
    content?: string | ReactNode;
}

export interface Notification {
    severity: 'info' | 'warning' | 'error' | 'success';
    uuid: string;
    title: string;
    content?: string | ReactNode;
}

export interface NotificationState {
    notifications: Notification[];
}

export interface ShowNotificationAction {
    type: NotificationActionType.SHOW,
    notification: ShowNotification
}

export interface DismissNotificationAction {
    type: NotificationActionType.DISMISS,
    uuid: string
}

export interface ClearNotificationsAction {
    type: NotificationActionType.CLEAR,
}

export type NotificationAction = ShowNotificationAction | DismissNotificationAction | ClearNotificationsAction;