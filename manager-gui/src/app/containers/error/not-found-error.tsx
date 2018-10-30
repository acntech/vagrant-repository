import * as React from 'react';
import { Component, ReactNode } from 'react';
import { Container, Message, Segment } from 'semantic-ui-react';

import { MainHeader } from '../../components';

interface ComponentProps {
    header?: boolean;
    icon: string;
    heading: string;
    content: string;
    children?: ReactNode;
}

class NotFoundErrorContainer extends Component<ComponentProps> {

    public render(): ReactNode {
        const { header, icon, heading, content, children } = this.props;

        return (
            <Container className="error error-not-found">
                {header ? <MainHeader /> : null}
                <Segment basic>
                    <Message
                        negative
                        icon={icon}
                        header={heading}
                        content={content} />
                    {children}
                </Segment>
            </Container>
        );
    }
}

export { NotFoundErrorContainer };