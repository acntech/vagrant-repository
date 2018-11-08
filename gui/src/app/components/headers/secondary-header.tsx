import * as React from 'react';
import { Component, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Header, Segment, Message } from 'semantic-ui-react'

import { Notification, NotificationState, RootState } from '../../models';
import { dismissNotification } from '../../state/actions';

interface ComponentStateProps {
    notificationState: NotificationState;
}

interface ComponentDispatchProps {
    dismissNotification: (uuid: string) => Promise<any>;
}

interface ComponentFieldProps {
    title?: string;
    subtitle?: string;
    children?: string | ReactNode;
}

type ComponentProps = ComponentFieldProps & ComponentDispatchProps & ComponentStateProps;

class SecondaryHeaderComponent extends Component<ComponentProps> {

    public render(): ReactNode {
        const { title, subtitle, children } = this.props;
        const { notifications } = this.props.notificationState;

        return (
            <div>
                <HeaderFragment title={title} subtitle={subtitle} children={children} />
                <MessagesFragment notifications={notifications} onDismissMessage={this.onDismissMessage} />
            </div>
        );
    }

    private onDismissMessage = (uuid: string) => {
        this.props.dismissNotification(uuid);
    }
}

const HeaderFragment: React.SFC<ComponentFieldProps> = (props) => {
    const { title, subtitle, children } = props;

    return (
        <Segment basic className="secondary-header">
            <Header>{children ? children : title}</Header>
            {subtitle ? <Header.Subheader>{subtitle}</Header.Subheader> : null}
        </Segment>
    );
}

interface MessagesFragmentProps {
    notifications: Notification[];
    onDismissMessage: (uuid: string) => void;
}

const MessagesFragment: React.SFC<MessagesFragmentProps> = (props) => {
    const { notifications, onDismissMessage } = props;

    if (notifications && notifications.length > 0) {
        return (
            <Segment basic className="secondary-header">
                {notifications.map((notification, index) => {
                    const { uuid, severity, title, content } = notification;
                    const info = severity === 'info';
                    const warning = severity === 'warning';
                    const error = severity === 'error';
                    const success = severity === 'success';
                    let icon = 'info circle';
                    if (error) {
                        icon = 'ban';
                    } else if (warning) {
                        icon = 'warning circle';
                    } else if (success) {
                        icon = 'check circle';
                    }
                    return <Message
                        key={index}
                        info={info}
                        warning={warning}
                        error={error}
                        success={success}
                        icon={icon}
                        header={title}
                        content={content}
                        onDismiss={() => onDismissMessage(uuid)} />;
                })}
            </Segment>
        );
    } else {
        return null;
    }
}

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    notificationState: state.notificationState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    dismissNotification: (uuid: string) => dispatch(dismissNotification(uuid))
});

const ConnectedSecondaryHeaderComponent = connect(mapStateToProps, mapDispatchToProps)(SecondaryHeaderComponent);

export { ConnectedSecondaryHeaderComponent as SecondaryHeader };