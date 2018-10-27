import * as React from 'react';
import { Component, ReactNode } from 'react';
import { Link } from 'react-router-dom';
import { Button, Container, Message, Segment } from 'semantic-ui-react';

interface RouteProps {
    history: any;
}

type ComponentProps = RouteProps;

class NotFoundContainer extends Component<ComponentProps> {
    public render(): ReactNode {
        return (
            <Container className="error error-not-found">
                <Segment basic>
                    <Message
                        negative
                        icon='blind'
                        header='Page not found'
                        content='Can not find the page you are looking for' />
                    <Link to='/'>
                        <Button primary icon='home' size='mini' content='Home' />
                    </Link>
                </Segment>
            </Container>
        );
    }
}

export { NotFoundContainer };