import {
    ClearNotificationsAction,
    DismissNotificationAction,
    NotificationActionType,
    NotificationType,
    ShowNotificationAction,
    ShowNotification
} from '../../models';

const showNotificationAction = (notification: ShowNotification): ShowNotificationAction => ({ type: NotificationActionType.SHOW, notification: notification });
const dismissNotificationAction = (uuid: string): DismissNotificationAction => ({ type: NotificationActionType.DISMISS, uuid: uuid });
const clearNotificationsAction = (): ClearNotificationsAction => ({ type: NotificationActionType.CLEAR });

interface Notification {
    type?: NotificationType;
    title: string;
    content?: string;
    timeout?: number;
}

export function showInfo(notification: Notification) {
    const { type } = notification;
    return showNotification({ ...notification, severity: 'info', type: type || NotificationType.GENERIC_INFO });
}

export function showWarning(notification: Notification) {
    const { type } = notification;
    return showNotification({ ...notification, severity: 'warning', type: type || NotificationType.GENERIC_WARNING });
}

export function showError(notification: Notification) {
    const { type } = notification;
    return showNotification({ ...notification, severity: 'error', type: type || NotificationType.GENERIC_ERROR });
}

export function showSuccess(notification: Notification) {
    const { type } = notification;
    return showNotification({ ...notification, severity: 'success', type: type || NotificationType.GENERIC_SUCCESS });
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