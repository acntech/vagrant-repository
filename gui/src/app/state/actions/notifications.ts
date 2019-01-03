import {
    ClearNotificationsAction,
    DismissNotificationAction,
    NotificationActionType,
    ShowNotificationAction,
    ShowNotification
} from '../../models';

const showNotificationAction = (notification: ShowNotification): ShowNotificationAction => ({ type: NotificationActionType.SHOW, notification: notification });
const dismissNotificationAction = (uuid: string): DismissNotificationAction => ({ type: NotificationActionType.DISMISS, uuid: uuid });
const clearNotificationsAction = (): ClearNotificationsAction => ({ type: NotificationActionType.CLEAR });

interface Notification {
    title: string;
    content?: string;
    timeout?: number;
}

export function showInfo(notification: Notification) {
    return showNotification({ ...notification, severity: 'info' });
}

export function showWarning(notification: Notification) {
    return showNotification({ ...notification, severity: 'warning' });
}

export function showError(notification: Notification) {
    return showNotification({ ...notification, severity: 'error' });
}

export function showSuccess(notification: Notification) {
    return showNotification({ ...notification, severity: 'success' });
}

export function showNotification(notification: ShowNotification) {
    return (dispatch) => {
        dispatch(showNotificationAction(notification));
    };
}

export function dismissNotification(uuid: string) {
    return (dispatch) => {
        dispatch(dismissNotificationAction(uuid));
    };
}

export function clearNotifications() {
    return (dispatch) => {
        dispatch(clearNotificationsAction());
    };
}