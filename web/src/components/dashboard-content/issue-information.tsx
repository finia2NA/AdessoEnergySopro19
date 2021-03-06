import React from 'react';
import { RouteComponentProps, Redirect, navigate } from '@reach/router';
import { useIssue } from '../../providers/issues-provider';
import { SubTitle, Span, Paragraph } from '../generics/text';
import { SecondaryButton, PrimaryButton } from '../generics/button';
import styles from './issue-information.module.scss';
import { useSnackBar } from '../../providers/snackbar-provider';

const IssueInformation: React.FC<RouteComponentProps<{ id: string }>> = ({
  id
}) => {
  const issue = useIssue(id || '');
  const showSnackbar = useSnackBar();

  if (!issue) {
    return <Redirect to="/" noThrow />;
  }

  function handleRequest() {
    if (!issue) return;
    navigate(`mailto:${issue.issue.email}`);
  }

  function resolve() {
    if (!issue) return;
    issue.updateIssue({ isClosed: true }).then(success => {
      if (success) showSnackbar('success', 'Ticker wurde geschlossen');
      else showSnackbar('error', 'Ticket konnte nicht geschlossen werden');
    });
  }

  return (
    <div className={styles.main}>
      <div className={styles.header}>
        <SubTitle>{issue.issue.subject}</SubTitle>
        <div>
          <SecondaryButton
            className={styles.secondButton}
            onClick={handleRequest}
          >
            Rückfrage stellen
          </SecondaryButton>
          <PrimaryButton disabled={issue.issue.isClosed} onClick={resolve}>
            {issue.issue.isClosed ? 'Erledigt' : 'Schließen'}
          </PrimaryButton>
        </div>
      </div>
      <div className={styles.nameText}>
        <Span className={styles.bold}> Name des Erstellers </Span>
        <Span>{issue.issue.name}</Span>
      </div>
      <div className={styles.emailText}>
        <Span className={styles.bold}> Email des Erstellers </Span>
        <Span>{issue.issue.email}</Span>
      </div>
      <div className={styles.message}>
        <Span className={styles.bold}>Nachricht</Span>
        <Paragraph> {issue.issue.message} </Paragraph>
      </div>
    </div>
  );
};

export default IssueInformation;
