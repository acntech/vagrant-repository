import { ReactNode } from 'react';
import { NotificationType, NotificationActionType } from '..';

export type Severity =
    | 'info'
    | 'warning'
    | 'error'
    | 'success';

export interface ShowNotification {
    severity: Severity;
    type: NotificationType;
    title: string;
    content?: string | ReactNode;
    timeout?: number;
}

export interface Notification {
    severity: Severity;
    type: NotificationType;
    uuid: string;
    title: string;
    content?: string | ReactNode;
    timeout?: number;
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