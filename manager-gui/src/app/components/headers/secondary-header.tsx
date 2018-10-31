import * as React from 'react';
import { Component, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Header, Segment, Message } from 'semantic-ui-react'

import { Notice, RootState } from '../../models';
import { noticesSelector } from '../../state/selectors';

interface ComponentStateProps {
    notices: Notice[];
}

interface ComponentDispatchProps {
}

interface ComponentFieldProps {
    title?: string;
    subtitle?: string;
    children?: string | ReactNode;
}

type ComponentProps = ComponentFieldProps & ComponentDispatchProps & ComponentStateProps;

class SecondaryHeaderComponent extends Component<ComponentProps> {

    public render(): ReactNode {
        const { title, subtitle, children, notices } = this.props;

        return (
            <Segment basic>
                <Header>{children ? children : title}</Header>
                {subtitle ? <Header.Subheader>{subtitle}</Header.Subheader> : null}
                <MessagesFragment notices={notices} />
            </Segment>
        );
    }
}

interface MessagesProps {
    notices: Notice[];
}

const MessagesFragment: React.SFC<MessagesProps> = (props) => {
    const { notices } = props;

    return (
        <div>
            {notices.map((notice, index) => {
                const { severity, header, content } = notice;
                const info = severity === 'info';
                const warning = severity === 'warning';
                const error = severity === 'error';
                let icon = 'info circle';
                if (error) {
                    icon = 'times circle';
                } else if (warning) {
                    icon = 'warning circle';
                }
                return <Message
                    key={index}
                    info={info}
                    warning={warning}
                    error={error}
                    icon={icon}
                    header={header}
                    content={content} />;
            })}
        </div>
    );
}

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    notices: noticesSelector(state)
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
});

const ConnectedSecondaryHeaderComponent = connect(mapStateToProps, mapDispatchToProps)(SecondaryHeaderComponent);

export { ConnectedSecondaryHeaderComponent as SecondaryHeader };